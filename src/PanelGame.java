import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PanelGame extends JPanel {
    private static final long serialVersionUID = -5428179568832879406L;
    private EntityPlayer player;
    private ModeArcade arcade;
    private ModeCampaign campaign;
    public Boolean start;
    private Boolean check;
    public Timer repaintTimer;
    public Timer moveTimer;
    public Timer powerTimer;
    private Timer textTimer;
    private Image killImage;
    public Boolean gameOver;
    public Boolean levelOver;
    public int gameMode;
    private String drawText;

    public PanelGame() {
        try {
            this.killImage = ImageIO.read(getClass().getResource("/resource/kill.png"));
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }

        initialize();

        this.repaintTimer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PanelGame.this.repaint();
            }
        });
        this.moveTimer = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EntityShot[] itr = Entities.getShotList().values().toArray(new EntityShot[0]);
                for (EntityShot shot : itr) {
                    Entities.moveShot(shot);
                }

                Iterator<EntityEnemyFace> itr2 = Entities.getEnemyList().values().iterator();
                while (itr2.hasNext()) {
                    Entities.fadeEnemy((EntityEnemyFace) itr2.next());
                }
            }
        });
        this.powerTimer = new Timer(43000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Entities.addPowerUp(1);
            }
        });
        this.textTimer = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PanelGame.this.drawText = null;
            }
        });
        this.textTimer.setRepeats(false);
    }

    public void initialize() {
        this.start = Boolean.valueOf(false);
        this.check = Boolean.valueOf(false);
        this.gameOver = Boolean.valueOf(false);
        this.levelOver = Boolean.valueOf(true);
        Controls.resetLocks();
        this.player = new EntityPlayer(600, 600);
        Entities.initialize(this.player);
        Collide.Initialize(this, this.player);
    }

    public void start(int gameMode) {
        this.gameMode = gameMode;
        if (gameMode == 1) {
            this.campaign = new ModeCampaign(this);
        } else if (gameMode == 2) {
            this.arcade = new ModeArcade(this);
        }
        if (!this.check.booleanValue()) {
            this.start = Boolean.valueOf(true);
            this.check = Boolean.valueOf(true);
        }
    }

    public void setText(String text) {
        this.drawText = text;
        if (this.textTimer.isRunning()) {
            this.textTimer.stop();
        }
        this.textTimer.start();
    }

    public Boolean getInvurnable() {
        return this.player.invurnable;
    }

    public Boolean getInvurnableState() {
        return this.player.invurnableState;
    }

    public void changeInvurnable() {
        if (!this.player.invurnable.booleanValue()) {
            this.player.invurnable = Boolean.valueOf(true);
        }
        if (this.player.invurnableState.booleanValue()) {
            this.player.invurnableState = Boolean.valueOf(false);
        } else {
            this.player.invurnableState = Boolean.valueOf(true);
        }
    }

    public void haltGame(Boolean gameOverStatus) {
        Controls.started = Boolean.valueOf(false);
        this.repaintTimer.stop();
        Entities.enemyCreateTimer.stop();
        this.powerTimer.stop();
        this.moveTimer.stop();

        if (gameOverStatus.booleanValue()) {
            this.gameOver = gameOverStatus;
            if (this.gameMode == 2) {
                this.arcade.arcadeSpawnTimer.stop();
                this.arcade.arcadeSpeedTimer.stop();
                this.arcade.arcadeGunInit.stop();
                this.arcade.arcadeGunTimer.stop();
            }
            this.textTimer.stop();
            this.drawText = null;
            repaint();

            Entities.getEnemyList().clear();
            Entities.getShotList().clear();
            Entities.getPowerList().clear();
            Entities.getDieList().clear();
            Controls.started = Boolean.valueOf(false);

            Timer timer = new Timer(2000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (PanelGame.this.player.invurnable.booleanValue()) {
                        PanelGame.this.player.invurnable = Boolean.valueOf(false);
                        AGameFrame.restart(Boolean.valueOf(true));
                    } else if (PanelGame.this.gameMode == 1) {
                        AGameFrame.setPanel(AGameFrame.h);
                    } else {
                        AGameFrame.setPanel(AGameFrame.p);
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Arial", 1, 28));
        drawPlayer(g);
        drawShot(g);
        drawEnemy(g);
        drawPowerUp(g);

        if (this.gameMode == 1) {
            this.campaign.levelChecks(g);
        } else if (this.start.booleanValue()) {
            this.arcade.startArcade(g);
        }
        if (this.gameOver.booleanValue()) {
            g.drawString("Game Over!!!", 175, getHeight() / 2);
        } else {
            Collide.collideCheck();
            drawText(g);
        }
    }

    private void drawPlayer(Graphics g) {
        int state = this.player.getState();
        if ((state >= 0) && (state <= 2)) {
            g.drawImage(this.player.getImage(1), (int)this.player.getX(), (int)this.player.getY(), null);
        } else {
            g.drawImage(this.player.getImage(2), (int)this.player.getX(), (int)this.player.getY(), null);
        }
    }

    private void drawShot(Graphics g) {
        Iterator<EntityShot> itr = Entities.getShotList().values().iterator();
        while (itr.hasNext()) {
            EntityShot shot = (EntityShot)itr.next();

            if ((shot.getDirection() == 1) || (shot.getDirection() == 3)) {
                g.fillRect(shot.x, shot.y, 15, 3);
            } else if (shot.getDirection() != 0) {
                g.fillRect(shot.x, shot.y, 3, 15);
            }
        }
    }

    private void drawEnemy(Graphics g) {
        Iterator<EntityEnemyFace> itr = Entities.getEnemyList().values().iterator();
        while (itr.hasNext()) {
            EntityEnemyFace entity = (EntityEnemyFace)itr.next();
            if (entity.isDead().booleanValue()) {
                g.drawImage(this.killImage, (int)entity.getX(), (int)entity.getY(), null);
                Entities.enemyDie(entity.getID());
            } else {
                g.drawImage(entity.getImage(entity.getState()), (int)entity.getX(), (int)entity.getY(), null);
            }
        }
    }

    private void drawPowerUp(Graphics g) {
        for (int i = 0; i < Entities.getPowerList().size(); i++) {
            EntityPowerUp powerup = (EntityPowerUp)Entities.getPowerList().get(i);
            g.drawImage(powerup.getImage(), (int)powerup.getX(), (int)powerup.getY(), null);
        }
    }

    private void drawText(Graphics g) {
        if (this.drawText != null) {
            g.drawString(this.drawText, 150, 30);
        }
    }
}