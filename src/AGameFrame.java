import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class AGameFrame extends JFrame implements KeyListener {
    private static final long serialVersionUID = -7534229374756325178L;
    public static PanelMenu m;
    public static PanelGame p;
    public static PanelHighscore h;
    public static JPanel gameFrame;
    private static JLabel shotsFired;
    private static JLabel enemysKilled;
    private static JLabel levelCount;
    public static int countShot;
    public static int countKilled;
    public static int countLevel;
    private static Boolean menuActive;
    private static CardLayout card;

    public static void main(String[] args) {
        new AGameFrame();
    }

    public AGameFrame() {
        setLayout(new BorderLayout());
        setSize(600, 628);

        JPanel north = new JPanel(new GridLayout(1, 1));
        north.setBorder(new LineBorder(Color.BLACK));
        JPanel innerNorth = new JPanel(new GridLayout(1, 3));
        innerNorth.setBorder(new EmptyBorder(5, 10, 5, 5));
        enemysKilled = new JLabel("Enemys killed: 0");
        shotsFired = new JLabel("Shots fired: 0");
        levelCount = new JLabel("Level: 0");
        innerNorth.add(shotsFired);
        innerNorth.add(enemysKilled);
        innerNorth.add(levelCount);
        north.add(innerNorth);

        card = new CardLayout();
        gameFrame = new JPanel(card);

        m = new PanelMenu();
        m.setFocusable(true);
        m.addKeyListener(this);

        p = new PanelGame();
        p.setFocusable(true);
        p.addKeyListener(this);

        Controls.initialize(p);
        countShot = 0;
        countKilled = 0;
        countLevel = 0;

        h = new PanelHighscore();
        h.setFocusable(true);
        h.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent arg0) {
                AGameFrame.h.setScore();
            }

            public void focusLost(FocusEvent arg0) { }
        });
        gameFrame.add(m, "1");
        gameFrame.add(p, "2");
        gameFrame.add(h, "3");

        add(north, "North");
        add(gameFrame, "Center");
        setVisible(true);

        setPanel(h);
    }

    public static void updateCount() {
        shotsFired.setText("Shots fired: " + countShot);
        enemysKilled.setText("Enemys killed: " + countKilled);
        if (countLevel == -1) {
            levelCount.setText("Level: ARCADE");
        } else {
            levelCount.setText("Level: " + countLevel);
        }
    }

    public static void setPanel(JPanel oldP) {
        if ((oldP instanceof PanelHighscore)) {
            menuActive = Boolean.valueOf(true);
            card.first(gameFrame);
            m.requestFocus();
        } else if ((oldP instanceof PanelMenu)) {
            card.next(gameFrame);
            p.requestFocus();
        } else if ((oldP instanceof PanelGame)) {
            card.last(gameFrame);
            h.requestFocus();
        }
    }

    public static void restart(Boolean fromHighscore) {
        if (p.gameOver.booleanValue()) {
            p.initialize();
            countShot = 0;
            countLevel = 0;
            countLevel = 0;
            updateCount();
        }
        Controls.started = Boolean.valueOf(true);
        menuActive = Boolean.valueOf(false);

        if (fromHighscore.booleanValue()) {
            setPanel(h);
        }
        p.start(m.getMode());
    }

    public void keyTyped(KeyEvent e) { }

    public void keyPressed(KeyEvent e) {
        if (menuActive.booleanValue()) {
            if (e.getKeyCode() == 38) {
                m.setPointer(2);
            } else if (e.getKeyCode() == 40) {
                m.setPointer(1);
            } else if (e.getKeyCode() == 10) {
                setPanel(m);
                restart(Boolean.valueOf(false));
            }
            repaint();
        } else {
            Controls.keyPressed(e.getKeyCode());
        }
    }

    public void keyReleased(KeyEvent e) {
        Controls.keyReleased(e.getKeyCode());
    }
}