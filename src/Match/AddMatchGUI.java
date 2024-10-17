package Match;

import DBConnection.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddMatchGUI extends JFrame {
    private JComboBox<String> homeTeamComboBox;
    private JComboBox<String> awayTeamComboBox;
    private JComboBox<String> venueComboBox;
    private JComboBox<String> refereeComboBox;
    private JComboBox<String> winnerTeamComboBox;
    private JTextField matchDateField;

    public AddMatchGUI() {
        // Set up the frame
        setTitle("Add Match");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setPadding(10);

        // Initialize combo boxes and text fields
        initializeComponents();

        // Load teams, venues, and referees into the combo boxes
        loadTeams();
        loadVenues();
        loadReferees();

        // Create and add action button
        JButton addButton = new JButton("Add Match");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMatch();
            }
        });

        // Add components to the frame
        addComponentsToFrame(addButton);
    }

    private void initializeComponents() {
        homeTeamComboBox = new JComboBox<>();
        awayTeamComboBox = new JComboBox<>();
        venueComboBox = new JComboBox<>();
        refereeComboBox = new JComboBox<>();
        winnerTeamComboBox = new JComboBox<>();
        matchDateField = new JTextField(10);
    }

    private void addComponentsToFrame(JButton addButton) {
        add(createLabeledComponent("Home Team:", homeTeamComboBox));
        add(createLabeledComponent("Away Team:", awayTeamComboBox));
        add(createLabeledComponent("Match Date (YYYY-MM-DD):", matchDateField));
        add(createLabeledComponent("Venue:", venueComboBox));
        add(createLabeledComponent("Referee:", refereeComboBox));
        add(createLabeledComponent("Winner Team (Optional):", winnerTeamComboBox));
        add(Box.createRigidArea(new Dimension(0, 10))); // Add space before the button
        add(addButton);
    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Add padding
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    private void loadTeams() {
        executeQueryAndPopulateComboBox("SELECT team_id, team_name FROM Team", homeTeamComboBox, awayTeamComboBox, winnerTeamComboBox);
    }

    private void loadVenues() {
        executeQueryAndPopulateComboBox("SELECT venue_id, venue_name FROM Venue", venueComboBox);
    }

    private void loadReferees() {
        executeQueryAndPopulateComboBox("SELECT referee_id, referee_name FROM Referee", refereeComboBox);
    }

    private void executeQueryAndPopulateComboBox(String query, JComboBox... comboBoxes) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                for (JComboBox comboBox : comboBoxes) {
                    comboBox.addItem(id + " - " + name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMatch() {
        String selectedHomeTeam = (String) homeTeamComboBox.getSelectedItem();
        String selectedAwayTeam = (String) awayTeamComboBox.getSelectedItem();
        String selectedVenue = (String) venueComboBox.getSelectedItem();
        String selectedReferee = (String) refereeComboBox.getSelectedItem();
        String selectedWinnerTeam = (String) winnerTeamComboBox.getSelectedItem();

        if (selectedHomeTeam == null || selectedAwayTeam == null || selectedVenue == null || selectedReferee == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        int homeTeamId = Integer.parseInt(selectedHomeTeam.split(" - ")[0]);
        int awayTeamId = Integer.parseInt(selectedAwayTeam.split(" - ")[0]);
        int venueId = Integer.parseInt(selectedVenue.split(" - ")[0]);
        int refereeId = Integer.parseInt(selectedReferee.split(" - ")[0]);
        Integer winnerTeamId = (selectedWinnerTeam != null && !selectedWinnerTeam.isEmpty())
                ? Integer.parseInt(selectedWinnerTeam.split(" - ")[0])
                : null;
        String matchDate = matchDateField.getText();

        // Ensure home and away teams are not the same
        if (homeTeamId == awayTeamId) {
            JOptionPane.showMessageDialog(this, "Home team and away team cannot be the same.");
            return;
        }

        // Check if match date is valid
        if (!isValidDate(matchDate)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid match date in the format YYYY-MM-DD.");
            return;
        }

        // Ensure winner is either home or away team
        if (winnerTeamId != null && (winnerTeamId != homeTeamId && winnerTeamId != awayTeamId)) {
            JOptionPane.showMessageDialog(this, "Winner team must be either the home team or the away team.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO `Match` (home_team_id, away_team_id, match_date, venue_id, referee_id, winner_team_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, homeTeamId);
                pstmt.setInt(2, awayTeamId);
                pstmt.setDate(3, Date.valueOf(matchDate)); // Convert String to Date
                pstmt.setInt(4, venueId);
                pstmt.setInt(5, refereeId);
                pstmt.setObject(6, winnerTeamId); // Use setObject for nullable fields
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Match added successfully!");
            clearFields();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            Date.valueOf(dateStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void handleSQLException(SQLException e) {
        if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
            JOptionPane.showMessageDialog(this, "Error: A match with the same teams, venue, and date already exists.");
        } else if (e.getMessage().contains("unique_match_per_day")) {
            JOptionPane.showMessageDialog(this, "Error: A match with the same teams already exists on this date.");
        } else if (e.getMessage().contains("unique_venue_per_day")) {
            JOptionPane.showMessageDialog(this, "Error: The selected venue is already booked for this date.");
        } else if (e.getMessage().contains("unique_referee_per_day")) {
            JOptionPane.showMessageDialog(this, "Error: The selected referee is already assigned to a match on this date.");
        } else {
            JOptionPane.showMessageDialog(this, "Error adding match: " + e.getMessage());
        }
    }

    private void clearFields() {
        matchDateField.setText("");
        homeTeamComboBox.setSelectedIndex(0);
        awayTeamComboBox.setSelectedIndex(0);
        venueComboBox.setSelectedIndex(0);
        refereeComboBox.setSelectedIndex(0);
        winnerTeamComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddMatchGUI addMatchGUI = new AddMatchGUI();
            addMatchGUI.setVisible(true);
        });
    }

    private void setPadding(int padding) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        getContentPane().add(panel);
    }
}
