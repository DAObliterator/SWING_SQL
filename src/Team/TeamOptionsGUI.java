package Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TeamOptionsGUI extends JFrame {

    public TeamOptionsGUI() {
        setTitle("Team Options");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addTeamButton = new JButton("Add Team");
        JButton viewTeamsButton = new JButton("View Teams");

        add(addTeamButton);
        add(viewTeamsButton);

        // Action listener to open AddTeamGUI
        addTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTeamGUI addTeamGUI = new AddTeamGUI();
                addTeamGUI.setVisible(true);
            }
        });

        // Action listener to open ViewTeamsGUI
        viewTeamsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewTeamsGUI viewTeamsGUI = new ViewTeamsGUI();
                viewTeamsGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        TeamOptionsGUI teamOptions = new TeamOptionsGUI();
        teamOptions.setVisible(true);
    }
}
