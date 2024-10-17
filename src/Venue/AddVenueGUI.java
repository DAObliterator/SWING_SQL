package Venue;

import DBConnection.DBConnection; // Ensure this points to your correct DB connection class
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddVenueGUI extends JFrame {
    private JTextField venueNameField, locationField, capacityField;

    public AddVenueGUI() {
        setTitle("Add Venue");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels and text fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Venue Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        venueNameField = new JTextField(15);
        add(venueNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        locationField = new JTextField(15);
        add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Capacity:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        capacityField = new JTextField(15);
        add(capacityField, gbc);

        // Add Venue button
        gbc.gridx = 1;
        gbc.gridy = 3;
        JButton submitButton = new JButton("Add Venue");
        submitButton.addActionListener(this::addVenue);
        add(submitButton, gbc);
    }

    // Method to insert a new venue into the database
    private void addVenue(ActionEvent e) {
        String venueName = venueNameField.getText().trim();
        String location = locationField.getText().trim();
        String capacityStr = capacityField.getText().trim();

        // Validate the input fields
        if (venueName.isEmpty() || location.isEmpty() || capacityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for capacity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert the data into the database
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO Venue (venue_name, location, capacity) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, venueName);
            pstmt.setString(2, location);
            pstmt.setInt(3, capacity);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Venue added successfully!");

            // Clear the fields after a successful insert
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding venue: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(pstmt, conn);
        }
    }

    // Method to clear all input fields
    private void clearFields() {
        venueNameField.setText("");
        locationField.setText("");
        capacityField.setText("");
    }

    // Method to close database main.resources
    private void closeResources(PreparedStatement pstmt, Connection conn) {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddVenueGUI addVenueGUI = new AddVenueGUI();
            addVenueGUI.setVisible(true);
        });
    }
}
