package Sponsor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SponsorOptionsGUI extends JFrame {

    public SponsorOptionsGUI() {
        setTitle("Sponsor Options");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addSponsorButton = new JButton("Add Sponsor");
        JButton viewSponsorsButton = new JButton("View Sponsors");

        add(addSponsorButton);
        add(viewSponsorsButton);

        // Action listener to open AddSponsorGUI
        addSponsorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddSponsorGUI addSponsorGUI = new AddSponsorGUI();
                addSponsorGUI.setVisible(true);
            }
        });

        // Action listener to open ViewSponsorsGUI
        viewSponsorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewSponsorsGUI viewSponsorsGUI = new ViewSponsorsGUI();
                viewSponsorsGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SponsorOptionsGUI sponsorOptionsGUI = new SponsorOptionsGUI();
        sponsorOptionsGUI.setVisible(true);
    }
}
