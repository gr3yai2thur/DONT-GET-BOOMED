import javax.swing.JFrame;

public class MainGame extends JFrame{

    MainGame(){
        setTitle("DONT GET BOOMED!");
        setSize(1920, 1080);
        setLayout(null);

        GamePanel gamePanel = new GamePanel();
        gamePanel.setBounds(0,0,1920,1080);
        add(gamePanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        new MainGame();
    }
}
   