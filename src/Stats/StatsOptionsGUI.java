package Stats;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatsOptionsGUI extends JFrame {

    public StatsOptionsGUI() {
        setTitle("Stats Options");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addStatsButton = new JButton("Add Stats");
        JButton viewStatsButton = new JButton("View Stats");

        add(addStatsButton);
        add(viewStatsButton);

        // Action listener to open AddStatsGUI
        addStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddStatsGUI addStatsGUI = new AddStatsGUI();
                addStatsGUI.setVisible(true);
            }
        });

        // Action listener to open ViewStatsGUI
        viewStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewStatsGUI viewStatsGUI = new ViewStatsGUI();
                viewStatsGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        StatsOptionsGUI statsOptionsGUI = new StatsOptionsGUI();
        statsOptionsGUI.setVisible(true);
    }
}
