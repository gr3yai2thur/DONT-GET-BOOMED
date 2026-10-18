import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Image;
import java.io.File;

public class GamePanel extends JPanel{
    private Image cave;

    public GamePanel(){
        cave = Toolkit.getDefaultToolkit().getImage(
            System.getProperty("user.dir") + File.separator + "images"
                + File.separator + "cave_map.png"
        );
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(cave, 0, 0, this);
    }
}