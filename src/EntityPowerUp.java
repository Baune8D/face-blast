import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EntityPowerUp extends Rectangle {
    private static final long serialVersionUID = 1818094897471700122L;
    private Image image;
    private int type;
    private int id;

    public EntityPowerUp(int type, int x, int y, int id) {
        super(x, y, 30, 15);
        this.type = type;
        this.x = x;
        this.y = y;
        this.id = id;

        String url = "";
        if (type == 0) {
            url = "/resource/gun.png";
        } else if (type == 1) {
            url = "/resource/boost.png";
        }
        try {
            this.image = ImageIO.read(getClass().getResource(url));
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    public Image getImage() {
        return this.image;
    }

    public int getID() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }
}