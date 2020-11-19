import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ModeCampaign {
    private PanelGame p;
    private int levelNumber;
    private int winCount;

    public ModeCampaign(PanelGame p) {
        this.p = p;
        this.levelNumber = 0;
        this.winCount = 8;
    }

    public int getLevelNumber() {
        return this.levelNumber;
    }

    private void newLevel(Graphics g) {
        this.levelNumber += 1;
        if (this.levelNumber == 4) {
            Entities.addPowerUp(0);
        }
        Timer ltime = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Entities.spawnTime -= 1000;
                Entities.enemySpeed += 1;
                ModeCampaign.this.winCount += 2;
                ModeCampaign.this.p.levelOver = Boolean.valueOf(false);
                AGameFrame.countKilled = 0;
                AGameFrame.countLevel = ModeCampaign.this.levelNumber;
                AGameFrame.updateCount();
                Controls.started = Boolean.valueOf(true);
                Controls.resetLocks();
                ModeCampaign.this.p.repaintTimer.start();
                Entities.enemyCreateTimer.setDelay(Entities.spawnTime);
                Entities.enemyCreateTimer.start();
                ModeCampaign.this.p.moveTimer.start();
                ModeCampaign.this.p.powerTimer.start();
            }
        });
        ltime.setRepeats(false);
        ltime.start();

        if (this.levelNumber == 5) {
            g.drawString("Last level!!", 175, 300);
        } else {
            g.drawString("Level " + this.levelNumber, 250, 300);
        }
    }

    public void levelChecks(Graphics g) {
        if (this.p.start.booleanValue()) {
            this.p.start = Boolean.valueOf(false);
            newLevel(g);
        } else if (this.levelNumber == 6) {
            this.p.haltGame(Boolean.valueOf(false));
            g.drawString("You have won!!!", 175, this.p.getHeight() / 2);
        } else if ((!this.p.levelOver.booleanValue()) &&
            (AGameFrame.countKilled >= this.winCount)) {
            this.p.levelOver = Boolean.valueOf(true);
            this.p.haltGame(Boolean.valueOf(false));
            newLevel(g);
        }
    }
}