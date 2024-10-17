package Sponsor;

import DBConnection.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.*;

public class AddSponsorGUI extends JFrame {
    private JComboBox<String> teamComboBox;
    private JTextField sponsorNameField;
    private JTextField sponsorValueField;

    public AddSponsorGUI() {
        setTitle("Add Sponsor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Initialize components
        teamComboBox = new JComboBox<>();
        sponsorNameField = new JTextField(15);
        sponsorValueField = new JTextField(15);

        // Team label and combo box
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing
        add(new JLabel("Team:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(teamComboBox, gbc);

        // Sponsor name label and text field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Sponsor Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(sponsorNameField, gbc);

        // Sponsor value label and text field
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Sponsor Value:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(sponsorValueField, gbc);

        // Add Sponsor button
        JButton addButton = new JButton("Add Sponsor");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addButton, gbc);

        // Add button action listener
        addButton.addActionListener(e -> addSponsor());

        loadTeams(); // Load teams into the combo box
    }

    // Load team data into the teamComboBox
    private void loadTeams() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT team_id, team_name FROM Team";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int teamId = rs.getInt("team_id");
                String teamName = rs.getString("team_name");
                teamComboBox.addItem(teamId + " - " + teamName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    // Add sponsor to the database
    private void addSponsor() {
        String selectedTeam = (String) teamComboBox.getSelectedItem();
        String sponsorName = sponsorNameField.getText().trim();
        String sponsorValueStr = sponsorValueField.getText().trim();

        // Validate input fields
        if (selectedTeam == null || sponsorName.isEmpty() || sponsorValueStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate that sponsor value is a valid number
        BigDecimal sponsorValue;
        try {
            sponsorValue = new BigDecimal(sponsorValueStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the sponsor value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int teamId = Integer.parseInt(selectedTeam.split(" - ")[0]);

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO Sponsor (sponsor_name, team_id, sponsor_value) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sponsorName);
            pstmt.setInt(2, teamId);
            pstmt.setBigDecimal(3, sponsorValue);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Sponsor added successfully!");
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding sponsor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    // Clear input fields after adding a sponsor
    private void clearFields() {
        sponsorNameField.setText("");
        sponsorValueField.setText("");
        teamComboBox.setSelectedIndex(0);
    }

    // Close database main.resources
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddSponsorGUI addSponsorGUI = new AddSponsorGUI();
            addSponsorGUI.setVisible(true);
        });
    }
}
