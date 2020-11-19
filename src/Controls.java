import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public abstract class Controls {
    private static PanelGame p;
    private static Timer moveTimer;
    private static Boolean lock;
    private static Boolean left;
    private static Boolean right;
    private static Boolean up;
    private static Boolean down;
    private static int speed1;
    private static int speed2;
    public static Boolean started;
    public static int shotDelay;

    public static void initialize(PanelGame p) {
        Controls.p = p;

        left = Boolean.valueOf(false);
        up = Boolean.valueOf(false);
        right = Boolean.valueOf(false);
        down = Boolean.valueOf(false);

        lock = Boolean.valueOf(false);
        started = Boolean.valueOf(false);
        shotDelay = 650;
        speed1 = 4;
        speed2 = speed1 - 1;

        moveTimer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Controls.controlPlayer(Controls.left, Controls.up, Controls.right, Controls.down);
            }
        });
        moveTimer.start();
    }

    public static void resetLocks() {
        left = Boolean.valueOf(false);
        up = Boolean.valueOf(false);
        right = Boolean.valueOf(false);
        down = Boolean.valueOf(false);
    }

    public static void setSpeed(int speed) {
        speed1 = speed;
        speed2 = speed1 - 1;
    }

    public static void shot(int direction) {
        Entities.addShot(direction);
        lock = Boolean.valueOf(true);
        Timer timer = new Timer(shotDelay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Controls.lock = Boolean.valueOf(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static void controlPlayer(Boolean left, Boolean up, Boolean right, Boolean down) {
        if (started.booleanValue()) {
            int x = (int)Entities.getPlayer().getX();
            int y = (int)Entities.getPlayer().getY();

            if (((left.booleanValue()) && (up.booleanValue())) || ((left.booleanValue()) && (down.booleanValue())) || ((right.booleanValue()) && (up.booleanValue())) || ((right.booleanValue()) && (down.booleanValue()))) {
                if ((left.booleanValue()) && (up.booleanValue())) {
                    Entities.movePlayer(x - speed2, y - speed2);
                } else if ((left.booleanValue()) && (down.booleanValue())) {
                    Entities.movePlayer(x - speed2, y + speed2);
                } else if ((right.booleanValue()) && (up.booleanValue())) {
                    Entities.movePlayer(x + speed2, y - speed2);
                } else if ((right.booleanValue()) && (down.booleanValue())) {
                    Entities.movePlayer(x + speed2, y + speed2);
                }
            } else if (left.booleanValue()) {
                Entities.movePlayer(x - speed1, y);
            } else if (right.booleanValue()) {
                Entities.movePlayer(x + speed1, y);
            } else if (up.booleanValue()) {
                Entities.movePlayer(x, y - speed1);
            } else if (down.booleanValue()) {
                Entities.movePlayer(x, y + speed1);
            }
        }
    }

    public static void keyPressed(int keyCode) {
        if (started.booleanValue()) {
            switch (keyCode) {
                case 37:
                    left = Boolean.valueOf(true);
                    break;
                case 38:
                    up = Boolean.valueOf(true);
                    break;
                case 39:
                    right = Boolean.valueOf(true);
                    break;
                case 40:
                    down = Boolean.valueOf(true);
                    break;
                default:
                    if (lock.booleanValue()) {
                        break;
                    }
                    switch (keyCode) {
                        case 65:
                            shot(1);
                            break;
                        case 87:
                            shot(2);
                            break;
                        case 68:
                            shot(3);
                            break;
                        case 83:
                            shot(4);
                    }
                    break;
            }
        } else if (keyCode == 10) {
            if (p.gameOver.booleanValue()) {
                p.initialize();
                AGameFrame.countShot = 0;
                AGameFrame.countLevel = 0;
                AGameFrame.countLevel = 0;
                AGameFrame.updateCount();
            }
            started = Boolean.valueOf(true);
            p.start(p.gameMode);
        }

        if (keyCode == 73) {
            if (p.getInvurnableState().booleanValue()) {
                p.setText("Invurnability deactivated!");
            } else {
                p.setText("Invurnability activated!");
            }
            p.changeInvurnable();
        }
    }

    public static void keyReleased(int keyCode) {
        if (started.booleanValue()) {
            switch (keyCode) {
                case 37:
                    left = Boolean.valueOf(false);
                    break;
                case 38:
                    up = Boolean.valueOf(false);
                    break;
                case 39:
                    right = Boolean.valueOf(false);
                    break;
                case 40:
                    down = Boolean.valueOf(false);
            }
        }
    }
}