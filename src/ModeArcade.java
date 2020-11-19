import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ModeArcade {
    private PanelGame p;
    public Timer arcadeSpawnTimer;
    public Timer arcadeSpeedTimer;
    public Timer arcadeGunInit;
    public Timer arcadeGunTimer;

    public ModeArcade(PanelGame p) {
        this.p = p;
        this.arcadeSpawnTimer = new Timer(30000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Entities.spawnTime != 800) {
                    Entities.spawnTime -= 200;
                } else {
                    ModeArcade.this.arcadeSpawnTimer.stop();
                }
            }
        });
        this.arcadeSpeedTimer = new Timer(60000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Entities.enemyCreateTimer.setDelay(Entities.spawnTime);
                if (Entities.enemySpeed < 5) {
                    Entities.enemySpeed += 1;
                } else {
                    ModeArcade.this.arcadeSpeedTimer.stop();
                }
            }
        });
        this.arcadeGunInit = new Timer(180000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ModeArcade.this.arcadeGunTimer.start();
            }
        });
        this.arcadeGunInit.setRepeats(false);

        this.arcadeGunTimer = new Timer(60000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Entities.addPowerUp(0);
            }
        });
    }

    public void startArcade(Graphics g) {
        this.p.start = Boolean.valueOf(false);
        this.p.levelOver = Boolean.valueOf(false);
        Timer ltime = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Entities.enemySpeed = 1;
                AGameFrame.countKilled = 0;
                AGameFrame.countLevel = -1;
                AGameFrame.updateCount();
                Controls.started = Boolean.valueOf(true);
                Controls.resetLocks();
                ModeArcade.this.p.repaintTimer.start();
                Entities.enemyCreateTimer.setDelay(Entities.spawnTime);
                Entities.enemyCreateTimer.start();
                ModeArcade.this.p.moveTimer.start();
                ModeArcade.this.p.powerTimer.start();
                ModeArcade.this.arcadeGunInit.start();
                ModeArcade.this.arcadeSpeedTimer.start();
                ModeArcade.this.arcadeSpawnTimer.start();
            }
        });
        ltime.setRepeats(false);
        ltime.start();

        g.drawString("SURVIVE!!!", 225, 300);
    }
}