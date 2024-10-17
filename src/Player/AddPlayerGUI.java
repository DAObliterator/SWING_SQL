package Player;

import DBConnection.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddPlayerGUI extends JFrame {
    private JTextField firstNameField, lastNameField, dobField;
    private JComboBox<String> teamComboBox, mentorComboBox, positionComboBox;

    // Define the five positions
    private final String[] positions = {"Point Guard", "Shooting Guard", "Small Forward", "Power Forward", "Center"};

    public AddPlayerGUI() {
        // Set up the frame
        setTitle("Add Player");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setPadding(10);

        // Initialize components
        initializeComponents();

        // Load teams and mentors into the combo boxes
        loadTeams();
        loadMentors();

        // Create and add action button
        JButton addButton = new JButton("Add Player");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayerToDatabase();
            }
        });

        // Add components to the frame
        addComponentsToFrame(addButton);
    }

    private void initializeComponents() {
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        dobField = new JTextField(10);
        teamComboBox = new JComboBox<>();
        mentorComboBox = new JComboBox<>();
        positionComboBox = new JComboBox<>(positions); // Use JComboBox for positions
    }

    private void addComponentsToFrame(JButton addButton) {
        add(createLabeledComponent("First Name:", firstNameField));
        add(createLabeledComponent("Last Name:", lastNameField));
        add(createLabeledComponent("Date of Birth (YYYY-MM-DD):", dobField));
        add(createLabeledComponent("Position:", positionComboBox)); // Use positionComboBox
        add(createLabeledComponent("Select Team:", teamComboBox));
        add(createLabeledComponent("Select Mentor:", mentorComboBox));
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
        executeQuery("SELECT team_id, team_name FROM Team", (rs) -> {
            while (rs.next()) {
                int teamId = rs.getInt("team_id");
                String teamName = rs.getString("team_name");
                teamComboBox.addItem(teamId + " - " + teamName); // Display both ID and name
            }
        });
    }

    private void loadMentors() {
        executeQuery("SELECT player_id, CONCAT(first_name, ' ', last_name) AS full_name FROM Player", (rs) -> {
            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String fullName = rs.getString("full_name");
                mentorComboBox.addItem(playerId + " - " + fullName); // Display both ID and name
            }
        });
    }

    private void executeQuery(String sql, ResultSetHandler handler) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            handler.handle(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPlayerToDatabase() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String dob = dobField.getText(); // YYYY-MM-DD format
        String position = (String) positionComboBox.getSelectedItem(); // Get selected position
        int teamId = getSelectedTeamId();
        Integer mentorId = getSelectedMentorId();

        if (areFieldsValid(firstName, lastName, dob, position)) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = createInsertStatement(conn, firstName, lastName, dob, position, teamId, mentorId)) {
                pstmt.executeUpdate();
                showSuccessMessage();
                resetFields();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorMessage(e.getMessage());
            }
        }
    }

    private boolean areFieldsValid(String firstName, String lastName, String dob, String position) {
        if (firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty() || position == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return false;
        }
        return true;
    }

    private boolean isMentorValid(Integer mentorId, int teamId) {
        if (mentorId != null) {
            Integer mentorTeamId = getMentorTeamId(mentorId);
            if (mentorTeamId == null) {
                JOptionPane.showMessageDialog(this, "Selected mentor does not belong to any team.");
                return false;
            }
            if (!mentorTeamId.equals(teamId)) {
                JOptionPane.showMessageDialog(this, "Mentor must be from the same team.");
                return false;
            }
        }
        return true;
    }

    private PreparedStatement createInsertStatement(Connection conn, String firstName, String lastName, String dob, String position, int teamId, Integer mentorId) throws SQLException {
        String sql = "INSERT INTO Player (first_name, last_name, dob, position, team_id, mentor_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, firstName);
        pstmt.setString(2, lastName);
        pstmt.setString(3, dob);
        pstmt.setString(4, position);
        pstmt.setInt(5, teamId);
        pstmt.setObject(6, mentorId); // Handle potential NULL value
        return pstmt;
    }

    private void showSuccessMessage() {
        JOptionPane.showMessageDialog(this, "Player added successfully!");
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, "Error adding player: " + message);
    }

    private void resetFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        dobField.setText("");
        positionComboBox.setSelectedIndex(0); // Reset combo box selection
        teamComboBox.setSelectedIndex(0); // Reset combo box selection
        mentorComboBox.setSelectedIndex(0); // Reset combo box selection
    }

    private int getSelectedTeamId() {
        String selectedTeam = (String) teamComboBox.getSelectedItem();
        return Integer.parseInt(selectedTeam.split(" - ")[0]); // Extract team ID from selection
    }

    private Integer getSelectedMentorId() {
        String selectedMentor = (String) mentorComboBox.getSelectedItem();
        if (selectedMentor != null) {
            return Integer.parseInt(selectedMentor.split(" - ")[0]); // Extract mentor ID
        }
        return null;
    }

    private Integer getMentorTeamId(Integer mentorId) {
        return executeSingleValueQuery("SELECT team_id FROM Player WHERE player_id = ?", mentorId);
    }

    private Integer executeSingleValueQuery(String sql, Integer value) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, value);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("team_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setPadding(int padding) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        getContentPane().add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddPlayerGUI addPlayerGUI = new AddPlayerGUI();
            addPlayerGUI.setVisible(true);
        });
    }

    // Functional interface for ResultSet handling
    interface ResultSetHandler {
        void handle(ResultSet rs) throws SQLException;
    }
}
