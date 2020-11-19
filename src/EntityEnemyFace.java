import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EntityEnemyFace extends Rectangle {
    private static final long serialVersionUID = -999657147011644533L;
    private Image image1;
    private Image image2;
    private int state;
    private int id;
    private int startSide;
    private int type;
    private Boolean dead;

    public EntityEnemyFace(int id, int x, int y, int side, int type) {
        super(x, y, type == 1 ? 28 : 30, type == 1 ? 29 : 35);
        try {
            if (type == 1) {
                this.image1 = ImageIO.read(getClass().getResource("/resource/enemy1.png"));
                this.image2 = ImageIO.read(getClass().getResource("/resource/enemy2.png"));
            } else if (type == 2) {
                this.image1 = ImageIO.read(getClass().getResource("/resource/robot1.png"));
                this.image2 = ImageIO.read(getClass().getResource("/resource/robot2.png"));
            }
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }

        this.state = 0;
        this.id = id;
        this.startSide = side;
        this.type = type;
        this.dead = Boolean.valueOf(false);
    }

    public Image getImage(int number) {
        if ((number >= 0) && (number <= 2)) {
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

    public int getID() {
        return this.id;
    }

    public int getStartSide() {
        return this.startSide;
    }

    public int getType() {
        return this.type;
    }

    public void setDead(Boolean state) {
        this.dead = state;
    }

    public Boolean isDead() {
        return this.dead;
    }
}