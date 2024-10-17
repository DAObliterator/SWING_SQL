package Match;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MatchOptionsGUI extends JFrame {

    public MatchOptionsGUI() {
        setTitle("Match Options");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addMatchButton = new JButton("Add Match");
        JButton viewMatchButton = new JButton("View Matches");

        add(addMatchButton);
        add(viewMatchButton);

        // Action listener to open AddMatchGUI
        addMatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddMatchGUI addMatchGUI = new AddMatchGUI();
                addMatchGUI.setVisible(true);
            }
        });

        // Action listener to open ViewMatchGUI
        viewMatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewMatchGUI viewMatchGUI = new ViewMatchGUI();
                viewMatchGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        MatchOptionsGUI matchOptionsGUI = new MatchOptionsGUI();
        matchOptionsGUI.setVisible(true);
    }
}
