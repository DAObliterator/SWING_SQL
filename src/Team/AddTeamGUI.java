package Team;

import DBConnection.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddTeamGUI extends JFrame {
    private JTextField teamNameField;
    private JTextField cityField;
    private JTextField ownerField;
    private JComboBox<String> venueComboBox;

    public AddTeamGUI() {
        setTitle("Add Team");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Form labels and input fields
        JLabel nameLabel = new JLabel("Team Name:");
        teamNameField = new JTextField();

        JLabel cityLabel = new JLabel("City:");
        cityField = new JTextField();

        JLabel ownerLabel = new JLabel("Owner:");
        ownerField = new JTextField();

        JLabel venueLabel = new JLabel("Select Venue:");
        venueComboBox = new JComboBox<>();

        // Load venue data into the combo box
        loadVenues();

        JButton addButton = new JButton("Add Team");

        // Add components to the layout
        add(nameLabel);
        add(teamNameField);
        add(cityLabel);
        add(cityField);
        add(ownerLabel);
        add(ownerField);
        add(venueLabel);
        add(venueComboBox);
        add(new JLabel()); // Empty space for layout alignment
        add(addButton);

        // Action listener for adding team
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTeamToDatabase();
            }
        });
    }

    // Load venue names into the combo box
    private void loadVenues() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT venue_id, venue_name FROM Venue"; // Ensure venue_name exists in the table
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // Populate combo box with venues
            while (rs.next()) {
                int venueId = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                venueComboBox.addItem(venueId + " - " + venueName); // Display ID and name
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Add the new team to the database
    private void addTeamToDatabase() {
        String teamName = teamNameField.getText();
        String city = cityField.getText();
        String owner = ownerField.getText();
        String selectedVenue = (String) venueComboBox.getSelectedItem();
        Integer venueId = null;

        // Validate form input
        if (selectedVenue != null) {
            venueId = Integer.parseInt(selectedVenue.split(" - ")[0]); // Extract venue ID from selection
        }

        if (teamName.isEmpty() || city.isEmpty() || owner.isEmpty() || venueId == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields and select a venue.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO Team (team_name, city, owner, home_venue_id) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, teamName);
            pstmt.setString(2, city);
            pstmt.setString(3, owner);
            pstmt.setInt(4, venueId); // Venue ID must not be null
            pstmt.executeUpdate();

            // Show success message and reset the form fields
            JOptionPane.showMessageDialog(this, "Team added successfully!");
            teamNameField.setText("");
            cityField.setText("");
            ownerField.setText("");
            venueComboBox.setSelectedIndex(0); // Reset combo box to first option
        } catch (SQLException e) {
            // Catch unique constraint violation error
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                JOptionPane.showMessageDialog(this, "Error: The selected venue is already assigned to another team.");
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding team: " + e.getMessage());
            }
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddTeamGUI addTeamGUI = new AddTeamGUI();
            addTeamGUI.setVisible(true);
        });
    }
}
