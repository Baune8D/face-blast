import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import javax.swing.Timer;

public abstract class Collide {
    private static PanelGame p;
    private static EntityPlayer player;

    public static void Initialize(PanelGame p, EntityPlayer player) {
        Collide.p = p;
        Collide.player = player;
    }

    public static void collideCheck() {
        Map<Integer, EntityShot> shotList = Entities.getShotList();
        Integer[] itr = shotList.keySet().toArray(new Integer[0]);
        for (Integer shotNumber : itr) {
            collideShot((EntityShot)shotList.get(shotNumber));
        }
        collidePowerUp();
        if (!player.invurnableState.booleanValue()) {
            collidePlayer();
        }
    }

    private static void collideShot(EntityShot shot) {
        int newid = shot.getID();
        int w = 15;
        int h = 3;
        if ((shot.getDirection() == 2) || (shot.getDirection() == 4)) {
            w = 3;
            h = 15;
        }

        Iterator<EntityEnemyFace> itr = Entities.getEnemyList().values().iterator();
        while (itr.hasNext()) {
            EntityEnemyFace enemy = (EntityEnemyFace)itr.next();
            if (!enemy.isDead().booleanValue()) {
                if (shot.checkEnemyShot().booleanValue()) {
                    if ((!player.invurnableState.booleanValue()) && (
                            (player.contains(shot.x, shot.y)) || (player.contains(shot.x + w, shot.y)) || (player.contains(shot.x, shot.y + h)) || (player.contains(shot.x + w, shot.y + h)))) {
                        Entities.deleteShot(newid);
                        p.haltGame(Boolean.valueOf(true));
                    }
                } else if ((enemy.contains(shot.x, shot.y)) || (enemy.contains(shot.x + w, shot.y)) || (enemy.contains(shot.x, shot.y + h)) || (enemy.contains(shot.x + w, shot.y + h))) {
                    Entities.deleteShot(newid);
                    enemy.setDead(Boolean.valueOf(true));
                    AGameFrame.countKilled += 1;
                    AGameFrame.updateCount();
                }
            }
        }
    }

    private static void collidePlayer() {
        int newx = (int)Entities.getPlayer().getX();
        int newy = (int)Entities.getPlayer().getY();
        EntityEnemyFace[] itr = Entities.getEnemyList().values().toArray(new EntityEnemyFace[0]);
        for (EntityEnemyFace enemy : itr) {
            EntityEnemyFace entity = enemy;
            if ((!entity.isDead().booleanValue()) && ((entity.contains(newx, newy)) || (entity.contains(newx + 17, newy)) || (entity.contains(newx, newy + 30)) || (entity.contains(newx + 17, newy + 30)))) {
                p.haltGame(Boolean.valueOf(true));
            }
        }
    }

    private static void collidePowerUp() {
        int newx = (int)Entities.getPlayer().getX();
        int newy = (int)Entities.getPlayer().getY();
        Iterator<EntityPowerUp> itr = Entities.getPowerList().iterator();
        while (itr.hasNext()) {
            EntityPowerUp powerup = (EntityPowerUp)itr.next();
            if ((powerup.contains(newx, newy)) || (powerup.contains(newx + 30, newy)) || (powerup.contains(newx, newy + 15)) || (powerup.contains(newx + 30, newy + 15))) {
                if (powerup.getType() == 0) {
                    Controls.shotDelay = 50;
                    if (p.gameMode == 2) {
                        Timer timer = new Timer(15000, new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                Controls.shotDelay = 650;
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                    p.setText("Machinegun acquired!");
                } else if (powerup.getType() == 1) {
                    Controls.setSpeed(16);
                    Timer timer = new Timer(10000, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Controls.setSpeed(4);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                    p.setText("Boost acquired!");
                }
                Entities.deletePowerUp(powerup.getID());
            }
        }
    }
}