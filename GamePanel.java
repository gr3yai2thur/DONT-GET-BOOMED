import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JPanel;

public class GamePanel extends JPanel {
    private Image cave, player;

    public GamePanel() {
        cave = Toolkit.getDefaultToolkit().getImage(
                System.getProperty("user.dir") + File.separator + "images"
                        + File.separator + "cave_map.png");

        player = Toolkit.getDefaultToolkit().getImage(
                System.getProperty("user.dir") + File.separator + "images"
                        + File.separator + "chibi_1.png");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(cave, 0, 0, this);
        g.drawImage(player, 200, 840, 80, 120, this);
    }
}