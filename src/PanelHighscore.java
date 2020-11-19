import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PanelHighscore extends JPanel implements ActionListener {
    private static final long serialVersionUID = -1332492233884765337L;
    private JTextField name;
    private JLabel kills;
    private int score;

    public PanelHighscore() {
        setLayout(new FlowLayout());
        setSize(150, 50);

        JPanel div = new JPanel(new GridLayout(4, 1));
        JLabel text = new JLabel("Submit to Highscore!");
        text.setFont(new Font("Arial", 1, 28));
        this.kills = new JLabel();
        JPanel submit = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Player Name: ");
        this.name = new JTextField(20);
        JButton button = new JButton("Submit");
        button.addActionListener(this);
        JButton button2 = new JButton("Restart");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AGameFrame.restart(Boolean.valueOf(true));
            }
        });
        submit.add(label);
        submit.add(this.name);
        submit.add(button);
        submit.add(button2);
        div.add(text);
        div.add(this.kills);
        div.add(submit);

        add(div);
        setVisible(true);
    }

    public void setScore() {
        this.score = AGameFrame.countKilled;
        this.kills.setText("Kills: " + this.score);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://<server>:3306/faceblast";
            Connection con = DriverManager.getConnection(url, "faceblast", "<password>");

            Statement s = con.createStatement();

            s.executeQuery("USE faceblast");
            s.executeQuery("SELECT Name, Score FROM highscore");

            ResultSet rs = s.getResultSet();

            ArrayList<String> names = new ArrayList<String>();
            ArrayList<Integer> scores = new ArrayList<Integer>();
            while (rs.next()) {
                names.add(rs.getString("Name"));
                scores.add(Integer.valueOf(Integer.parseInt(rs.getString("Score"))));
            }

            int pos = -1;
            for (int i = 0; i < scores.size(); i++) {
                if (((Integer)scores.get(i)).intValue() < this.score) {
                    pos = i;
                    break;
                }
            }

            if (pos != -1) {
                names.add(pos, this.name.getText());
                names.remove(names.size() - 1);
                scores.add(pos, Integer.valueOf(this.score));
                scores.remove(scores.size() - 1);

                for (int i = 0; i < 10; i++) {
                    s.executeUpdate("UPDATE highscore SET Name = '" + (String)names.get(i) + "', Score = " + scores.get(i) + " WHERE Placement = " + (i + 1));
                }
            }

            s.close();
            con.close();
            AGameFrame.restart(Boolean.valueOf(true));
        } catch (Exception f) {
            System.out.println(f.getMessage());
        }
    }
}