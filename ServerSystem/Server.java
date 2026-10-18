package ServerSystem;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    public static final int PORT = 5555;
    public static final int MIN_PLAYERS = 3;
    public static final int MAX_PLAYERS = 5;

    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static boolean gameStarted = false;

    public static void main(String[] args) {
        IO.println("==================================================");
        IO.println(" [DON'T GET BOOMED] Server Started on Port: " + PORT);
        IO.println(" Waiting for " + MIN_PLAYERS + " - " + MAX_PLAYERS + " players to join...");
        IO.println("==================================================");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                if (clients.size() >= MAX_PLAYERS) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("Server is full. Connection refused.");
                    socket.close();
                    continue;
                }

                ClientHandler client = new ClientHandler(socket);
                clients.add(client);
                new Thread(client).start();

                IO.println("[+] ผู้เล่นใหม่เชื่อมต่อเข้ามา: ปัจจุบันมี " + clients.size() + "/" + MAX_PLAYERS + " คน");

                broadcast("PLAYER_COUNT," + clients.size());
            }
        } catch (IOException e) {
            IO.println("Error starting server: " + e.getMessage());
        }
    }

    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        IO.println("[-] ผู้เล่นออกจากเซิร์ฟเวอร์: ปัจจุบันมี " + clients.size() + "/" + MAX_PLAYERS + " คน");
        broadcast("PLAYER_COUNT," + clients.size());
        broadcast("PLAYER_LEFT," + client.playerId);
    }

    static class ClientHandler implements Runnable {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String playerId;
        private String playerName = "Player";

        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.playerId = "P" + (clients.size() + 1);
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                sendMessage("YOUR_ID," + playerId);

                String line;
                while ((line = in.readLine()) != null) {
                    if(line.trim().isEmpty()) continue;
                    handleCommand(line.trim());
                }
            } catch (Exception e) {
                
            } finally {
                removeClient(this);
                try {
                    socket.close();
                } catch (IOException ignored) {
                    
                }
            }
        }

        public void handleCommand(String line) {
            String[] parts = line.split(",");
            String command = parts[0];

            switch (command) {
                case "NAME":
                    if(parts.length > 1) this.playerName = parts[1];
                    broadcast("CHAT," + playerName + " เข้าร่วมเกมแล้ว!");
                    break;
                
                case "START":
                    if(clients.size() >= MIN_PLAYERS && clients.size() <= MAX_PLAYERS) {
                        gameStarted = true;
                        broadcast("START_GAME");
                        IO.println(">>> เริ่มเกมแล้ว! (มีผู้เล่น " + clients.size() + " คน) <<<");
                    } else {
                        sendMessage("ERROR,ต้องมีผู้เล่น 3-5 คนเพื่อเริ่มเกม (ขณะนี้มี " + clients.size() + " คน)");
                    }
                    break;
                
                case "MOVE":
                    if(parts.length >= 4) {
                        broadcast("PLAYER_MOVED," + playerId + "," + parts[1] + "," + parts[2] + "," + parts[3]);
                    }
                    break;

                case "BOOM":
                    broadcast("PLAYER_DEAD," + playerId);
                    break;
                
                case "CHAT":
                    if(parts.length > 1) {
                        broadcast("CHAT," + playerName + ": " + parts[1]);
                    }
                    break;
                
                default:
                    IO.println("คำสั่งไม่รู้จักจาก " + playerId + ": " + line);
                    break;
            }
        }

        public void sendMessage(String message) {
            if (out != null)
                out.println(message);
        }
    }
}
