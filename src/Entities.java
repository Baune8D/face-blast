import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.Timer;

public abstract class Entities implements ActionListener {
    private static EntityPlayer player;
    private static ActionListener action;
    public static Timer enemyCreateTimer;
    public static int entityID;
    public static int enemySpeed;
    public static int spawnTime;
    private static int enemyTypeCount;
    private static LinkedList<Integer> dieList;
    private static HashMap<Integer, EntityShot> shotList;
    private static HashMap<Integer, EntityEnemyFace> enemyList;
    private static LinkedList<EntityPowerUp> powerList;
    private static int tmpid;

    public static void initialize(EntityPlayer player) {
        Entities.player = player;
        entityID = 0;
        spawnTime = 5000;
        enemyTypeCount = 1;
        dieList = new LinkedList<Integer>();
        shotList = new HashMap<Integer, EntityShot>();
        enemyList = new HashMap<Integer, EntityEnemyFace>();
        powerList = new LinkedList<EntityPowerUp>();
        makeActionListener();
        enemyCreateTimer = new Timer(spawnTime, action);
    }

    public static int getEntityID() {
        return entityID;
    }

    public static HashMap<Integer, EntityShot> getShotList() {
        return shotList;
    }

    public static void addShot(int dir) {
        addShot(dir, (int)player.getX(), (int)player.getY(), Boolean.valueOf(false));
    }

    public static void addShot(int dir, int x, int y, Boolean enemyShot) {
        shotList.put(Integer.valueOf(entityID), new EntityShot(dir, x, y, entityID, enemyShot));
        entityID += 1;
        if (!enemyShot.booleanValue()) {
            AGameFrame.countShot += 1;
            AGameFrame.updateCount();
        }
    }

    public static void deleteShot(int id) {
        shotList.remove(Integer.valueOf(id));
    }

    public static void moveShot(EntityShot shot) {
        switch (shot.getDirection()) {
            case 1:
                shot.x -= 15;
                break;
            case 2:
                shot.y -= 15;
                break;
            case 3:
                shot.x += 15;
                break;
            case 4:
                shot.y += 15;
        }

        if ((shot.x > AGameFrame.gameFrame.getWidth()) || (shot.x < -15) || (shot.y > AGameFrame.gameFrame.getHeight()) || (shot.y < -15)) {
            deleteShot(shot.getID());
        }
    }

    public static HashMap<Integer, EntityEnemyFace> getEnemyList() {
        return enemyList;
    }

    public static LinkedList<Integer> getDieList() {
        return dieList;
    }

    private static void deleteEnemy(int id) {
        enemyList.remove(Integer.valueOf(id));
    }

    public static void enemyDie(int id) {
        dieList.add(Integer.valueOf(id));
        Timer dtime = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!Entities.dieList.isEmpty()) {
                    Entities.deleteEnemy(((Integer)Entities.dieList.get(0)).intValue());
                    Entities.dieList.remove(0);
                }
            }
        });
        dtime.setRepeats(false);
        dtime.start();
    }

    public static void moveEnemy(EntityEnemyFace enemy) {
        if (!enemy.isDead().booleanValue()) {
            int disX = (int)player.getX() - (int)enemy.getX();
            int disY = (int)player.getY() - (int)enemy.getY();
            int speed;
            if ((enemy.getType() == 2) && (enemySpeed > 1)) {
                speed = enemySpeed - 1;
            } else {
                speed = enemySpeed;
            }
            int dir;
            if (Math.abs(disX) > Math.abs(disY)) {
                if (disX < 0) {
                    enemy.x -= speed;
                    dir = 1;
                } else {
                    enemy.x += speed;
                    dir = 3;
                }
            } else {
                if (disY < 0) {
                    enemy.y -= speed;
                    dir = 2;
                } else {
                    enemy.y += speed;
                    dir = 4;
                }
            }
            enemy.changeState();

            if (enemy.getType() == 2) {
                EntityEnemyRobot robot = (EntityEnemyRobot)enemy;
                if ((!robot.timerIsRunning().booleanValue()) && ((enemy.contains(player.x, enemy.y)) || (enemy.contains(player.x + 17, enemy.y)) || (enemy.contains(enemy.x, player.y)) || (enemy.contains(enemy.x, player.y + 30)))) {
                    addShot(dir, enemy.x, enemy.y, Boolean.valueOf(true));
                    robot.startShotTimer();
                }
            }
        }
    }

    public static void fadeEnemy(EntityEnemyFace enemy) {
        int width = (int)enemy.getWidth();
        int height = (int)enemy.getHeight();
        if ((enemy.getStartSide() == 0) && (enemy.getY() < 0.0D)) {
            enemy.y += enemySpeed;
        } else if ((enemy.getStartSide() == 1) && (enemy.getY() > AGameFrame.gameFrame.getHeight() - height)) {
            enemy.y -= enemySpeed;
        } else if ((enemy.getStartSide() == 2) && (enemy.getX() < 0.0D)) {
            enemy.x += enemySpeed;
        } else if ((enemy.getStartSide() == 3) && (enemy.getX() > AGameFrame.gameFrame.getWidth() - width)) {
            enemy.x -= enemySpeed;
        } else {
            moveEnemy(enemy);
        }
    }

    public static EntityPlayer getPlayer() {
        return player;
    }

    public static void movePlayer(int x, int y) {
        if ((x <= AGameFrame.gameFrame.getWidth() - 17) && (x >= 0) && (y <= AGameFrame.gameFrame.getHeight() - 29) && (y >= 0)) {
            player.changeState();
            player.x = x;
            player.y = y;
        }
    }

    public static LinkedList<EntityPowerUp> getPowerList() {
        return powerList;
    }

    public static void addPowerUp(int type) {
        Random rand = new Random();
        int x = rand.nextInt(AGameFrame.gameFrame.getWidth() - 30);
        int y = rand.nextInt(AGameFrame.gameFrame.getHeight() - 30);
        powerList.add(new EntityPowerUp(type, x, y, entityID));
        if ((AGameFrame.countLevel == -1) || (type != 0)) {
            timeOut(entityID, type);
        }
        entityID += 1;
    }

    public static void deletePowerUp(int id) {
        for (int i = 0; i < powerList.size(); i++) {
            if (((EntityPowerUp)powerList.get(i)).getID() == id) {
                powerList.remove(i);
            }
        }
    }

    public static void timeOut(int id, int type) {
        tmpid = id;
        int time;
        if (type == 0) {
            time = 15000;
        } else {
            time = 10000;
        }
        Timer timeOut = new Timer(time, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Entities.deletePowerUp(Entities.tmpid);
            }
        });
        timeOut.setRepeats(false);
        timeOut.start();
    }

    public static void makeActionListener() {
        action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int robotChance = AGameFrame.countLevel == -1 ? 8 : 6;
                int type;
                if ((AGameFrame.countLevel > 1) || (AGameFrame.countLevel == -1)) {
                    if (Entities.enemyTypeCount % robotChance == 0) {
                        type = 2;
                    } else {
                        type = 1;
                    }
                    Entities.enemyTypeCount += 1;
                } else {
                    type = 1;
                }
                Random rand = new Random();
                int side = rand.nextInt(4);
                int number;
                if ((side == 0) || (side == 1)) {
                    number = rand.nextInt(AGameFrame.gameFrame.getWidth() - 29);
                } else {
                    number = rand.nextInt(AGameFrame.gameFrame.getHeight() - 29);
                }
                if (type == 1) {
                    if (side == 0) {
                        Entities.enemyList.put(Integer.valueOf(Entities.entityID), new EntityEnemyFace(Entities.entityID, number, -29, 0, 1));
                    } else if (side == 1) {
                        Entities.enemyList.put(Integer.valueOf(Entities.entityID), new EntityEnemyFace(Entities.entityID, number, AGameFrame.gameFrame.getHeight(), 1, 1));
                    } else if (side == 2) {
                        Entities.enemyList.put(Integer.valueOf(Entities.entityID), new EntityEnemyFace(Entities.entityID, -29, number, 2, 1));
                    } else {
                        Entities.enemyList.put(Integer.valueOf(Entities.entityID), new EntityEnemyFace(Entities.entityID, AGameFrame.gameFrame.getWidth(), number, 3, 1));
                    }
                } else if (type == 2) {
                    if (side == 0) {
                        Entities.enemyList.put(Integer.valueOf(Entities.entityID), new EntityEnemyRobot(Entities.entityID, number, -29, 0));
                    } else if (side == 1) {
                        Entities.enemyList.put(Integer.valueOf(Entities.entityID), new EntityEnemyRobot(Entities.entityID, number, AGameFrame.gameFrame.getHeight(), 1));
                    } else if (side == 2) {
                        Entities.enemyList.put(Integer.valueOf(Entities.entityID), new EntityEnemyRobot(Entities.entityID, -29, number, 2));
                    } else {
                        Entities.enemyList.put(Integer.valueOf(Entities.entityID), new EntityEnemyRobot(Entities.entityID, AGameFrame.gameFrame.getWidth(), number, 3));
                    }
                }
                Entities.entityID += 1;
            }
        };
    }
}