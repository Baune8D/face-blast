import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class PanelHighscore extends JPanel implements ActionListener {
    private static final long serialVersionUID = -1332492233884765337L;
    // The highscore database, e.g. jdbc:mysql://localhost:3306/faceblast
    private static final String DB_URL = System.getenv("FACEBLAST_DB_URL");
    private static final String DB_USER = System.getenv("FACEBLAST_DB_USER");
    private static final String DB_PASSWORD = System.getenv("FACEBLAST_DB_PASSWORD");
    // The length of the Name column
    private static final int MAX_NAME_LENGTH = 20;
    private static final int LOGIN_TIMEOUT_SECONDS = 10;
    private static final int STATUS_WIDTH = 450;
    private JTextField name;
    private JLabel kills;
    private JLabel status;
    private JButton submit;
    private JButton restart;
    private boolean submitting;
    private int score;

    public PanelHighscore() {
        setLayout(new FlowLayout());
        setSize(150, 50);

        JPanel div = new JPanel(new GridLayout(4, 1));
        JLabel text = new JLabel("Submit to Highscore!");
        text.setFont(new Font("Arial", 1, 28));
        this.kills = new JLabel();
        JPanel submitPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Player Name: ");
        this.name = new JTextField(MAX_NAME_LENGTH);
        this.submit = new JButton("Submit");
        this.submit.addActionListener(this);
        this.restart = new JButton("Restart");
        this.restart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AGameFrame.restart(Boolean.valueOf(true));
            }
        });
        this.status = new JLabel();
        submitPanel.add(label);
        submitPanel.add(this.name);
        submitPanel.add(this.submit);
        submitPanel.add(this.restart);
        div.add(text);
        div.add(this.kills);
        div.add(submitPanel);
        div.add(this.status);

        add(div);
        setVisible(true);
    }

    public void setScore() {
        if (this.submitting) {
            return;
        }
        this.score = AGameFrame.countKilled;
        this.kills.setText("Kills: " + this.score);
        if (isConfigured()) {
            showStatus("");
            this.submit.setEnabled(true);
        } else {
            showStatus("Highscores aren't set up: set FACEBLAST_DB_URL to submit.");
            this.submit.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        final String playerName = this.name.getText().trim();
        if (playerName.isEmpty()) {
            showStatus("Enter a player name.");
            return;
        }
        if (playerName.length() > MAX_NAME_LENGTH) {
            showStatus("Use at most " + MAX_NAME_LENGTH + " characters for the name.");
            return;
        }

        final int submittedScore = this.score;
        setSubmitting(true);
        showStatus("Submitting...");
        // Talk to the database off the event dispatch thread, so the window doesn't freeze
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws SQLException {
                submitScore(playerName, submittedScore);
                return null;
            }

            protected void done() {
                setSubmitting(false);
                try {
                    get();
                    AGameFrame.restart(Boolean.valueOf(true));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    ex.getCause().printStackTrace();
                    showStatus("Couldn't submit the highscore: " + firstLine(ex.getCause().getMessage()));
                }
            }
        }.execute();
    }

    // Wrap the text, so a long error message doesn't widen the panel
    private void showStatus(String text) {
        String escaped = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        this.status.setText("<html><body style='width: " + STATUS_WIDTH + "px'>" + escaped + "</body></html>");
    }

    private void setSubmitting(boolean submitting) {
        this.submitting = submitting;
        this.submit.setEnabled(!submitting);
        this.restart.setEnabled(!submitting);
    }

    private static boolean isConfigured() {
        return DB_URL != null && !DB_URL.isBlank();
    }

    private static String firstLine(String message) {
        if (message == null) {
            return "unknown error";
        }
        int end = message.indexOf('\n');
        return end == -1 ? message : message.substring(0, end);
    }

    // The highscore table has a row for each placement, from 1 to 10. A new
    // score takes the placement of the first lower score, and the scores below
    // it move down one placement.
    private static void submitScore(String playerName, int score) throws SQLException {
        DriverManager.setLoginTimeout(LOGIN_TIMEOUT_SECONDS);
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            con.setAutoCommit(false);
            try {
                List<Integer> placements = new ArrayList<>();
                List<String> names = new ArrayList<>();
                List<Integer> scores = new ArrayList<>();
                // FOR UPDATE locks the rows, so two players submitting at once can't overwrite each other
                try (PreparedStatement select = con.prepareStatement("SELECT Placement, Name, Score FROM highscore ORDER BY Placement FOR UPDATE");
                        ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        placements.add(rs.getInt("Placement"));
                        names.add(rs.getString("Name"));
                        scores.add(rs.getInt("Score"));
                    }
                }

                int pos = -1;
                for (int i = 0; i < scores.size(); i++) {
                    if (scores.get(i) < score) {
                        pos = i;
                        break;
                    }
                }

                if (pos != -1) {
                    names.add(pos, playerName);
                    names.remove(names.size() - 1);
                    scores.add(pos, score);
                    scores.remove(scores.size() - 1);

                    try (PreparedStatement update = con.prepareStatement("UPDATE highscore SET Name = ?, Score = ? WHERE Placement = ?")) {
                        for (int i = pos; i < placements.size(); i++) {
                            update.setString(1, names.get(i));
                            update.setInt(2, scores.get(i));
                            update.setInt(3, placements.get(i));
                            update.addBatch();
                        }
                        update.executeBatch();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
