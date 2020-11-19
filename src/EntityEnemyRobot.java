import javax.swing.Timer;

public class EntityEnemyRobot extends EntityEnemyFace {
    private static final long serialVersionUID = 2753199101020979743L;
    private Timer shotTimer;

    public EntityEnemyRobot(int id, int x, int y, int side) {
        super(id, x, y, side, 2);
        this.shotTimer = new Timer(1000, null);
        this.shotTimer.setRepeats(false);
    }

    public Boolean timerIsRunning() {
        return Boolean.valueOf(this.shotTimer.isRunning());
    }

    public void startShotTimer() {
        this.shotTimer.start();
    }
}