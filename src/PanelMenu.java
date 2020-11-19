import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PanelMenu extends JPanel {
    private static final long serialVersionUID = -736924809932047085L;
    private Image image;
    private int y;

    public PanelMenu() {
        try {
            this.image = ImageIO.read(getClass().getResource("/resource/enemy1.png"));
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        this.y = 75;
    }

    public void setPointer(int dir) {
        if (dir == 1) {
            this.y = 145;
        } else {
            this.y = 75;
        }
    }

    public int getMode() {
        if (this.y == 75) {
            return 1;
        }
        return 2;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Arial", 1, 28));
        g.drawString("Campaign Mode", 175, 100);
        g.drawString("Survival Mode", 175, 170);
        g.drawImage(this.image, 125, this.y, 28, 29, null);
    }
}