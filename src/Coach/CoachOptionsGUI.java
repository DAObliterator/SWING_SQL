package Coach;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CoachOptionsGUI extends JFrame {

    public CoachOptionsGUI() {
        setTitle("Coach Options");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addCoachButton = new JButton("Add Coach");
        JButton viewCoachesButton = new JButton("View Coaches");

        add(addCoachButton);
        add(viewCoachesButton);

        // Action listener to open AddCoachGUI
        addCoachButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCoachGUI addCoachGUI = new AddCoachGUI();
                addCoachGUI.setVisible(true);
            }
        });

        // Action listener to open ViewCoachesGUI
        viewCoachesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewCoachesGUI viewCoachesGUI = new ViewCoachesGUI();
                viewCoachesGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        CoachOptionsGUI coachOptions = new CoachOptionsGUI();
        coachOptions.setVisible(true);
    }
}
