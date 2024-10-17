package Referee;

import DBConnection.DBConnection; // Update this line
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RefereeOptionsGUI extends JFrame {

    public RefereeOptionsGUI() {
        setTitle("Referee Options");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addRefereeButton = new JButton("Add Referee");
        JButton viewRefereeButton = new JButton("View Referees");

        add(addRefereeButton);
        add(viewRefereeButton);

        // Add action listener to open AddRefereeGUI
        addRefereeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddRefereeGUI addRefereeGUI = new AddRefereeGUI();
                addRefereeGUI.setVisible(true);
            }
        });

        // Add action listener to open ViewRefereesGUI
        viewRefereeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewRefereesGUI viewRefereesGUI = new ViewRefereesGUI();
                viewRefereesGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        RefereeOptionsGUI refereeOptions = new RefereeOptionsGUI();
        refereeOptions.setVisible(true);
    }
}
