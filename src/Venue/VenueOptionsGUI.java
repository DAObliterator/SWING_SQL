package Venue;

import DBConnection.DBConnection; // Update this line
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VenueOptionsGUI extends JFrame {

    public VenueOptionsGUI() {
        setTitle("Venue Options");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addVenueButton = new JButton("Add Venue");
        JButton viewVenuesButton = new JButton("View Venues");

        add(addVenueButton);
        add(viewVenuesButton);

        // Add action listener to open AddVenueGUI
        addVenueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddVenueGUI addVenueGUI = new AddVenueGUI();
                addVenueGUI.setVisible(true);
            }
        });

        // Add action listener to open ViewVenuesGUI
        viewVenuesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewVenuesGUI viewVenuesGUI = new ViewVenuesGUI();
                viewVenuesGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        VenueOptionsGUI venueOptions = new VenueOptionsGUI();
        venueOptions.setVisible(true);
    }
}
