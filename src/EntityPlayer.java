import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EntityPlayer extends Rectangle {
    private static final long serialVersionUID = 8220527634720098983L;
    private Image image1;
    private Image image2;
    private int state;
    public Boolean invurnableState;
    public Boolean invurnable;

    public EntityPlayer(int width, int height) {
        super(width / 2 - 10, height / 2 + 10, 17, 30);
        try {
            this.image1 = ImageIO.read(getClass().getResource("/resource/player1.png"));
            this.image2 = ImageIO.read(getClass().getResource("/resource/player2.png"));
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        this.state = 0;
        this.invurnable = Boolean.valueOf(false);
        this.invurnableState = Boolean.valueOf(false);
    }

    public Image getImage(int number) {
        if (number == 1) {
            return this.image1;
        }
        return this.image2;
    }

    public int getState() {
        return this.state;
    }

    public void changeState() {
        if (this.state == 6) {
            this.state = 0;
        } else {
            this.state += 1;
        }
    }
}