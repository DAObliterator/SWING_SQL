package Coach;

import DBConnection.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddCoachGUI extends JFrame {
    private JComboBox<String> teamComboBox;
    private JTextField coachNameField;
    private JTextField experienceYearsField;

    public AddCoachGUI() {
        setTitle("Add Coach");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        teamComboBox = new JComboBox<>();
        coachNameField = new JTextField();
        experienceYearsField = new JTextField();

        add(new JLabel("Coach Name:"));
        add(coachNameField);
        add(new JLabel("Experience Years:"));
        add(experienceYearsField);
        add(new JLabel("Team:"));
        add(teamComboBox);

        JButton addButton = new JButton("Add Coach");
        add(addButton);

        // Load teams into the combo box
        loadTeams();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCoach();
            }
        });
    }

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

    private void addCoach() {
        String selectedTeam = (String) teamComboBox.getSelectedItem();
        String coachName = coachNameField.getText();
        String experienceYearsStr = experienceYearsField.getText();

        if (selectedTeam != null && !coachName.trim().isEmpty() && !experienceYearsStr.trim().isEmpty()) {
            int teamId = Integer.parseInt(selectedTeam.split(" - ")[0]);
            int experienceYears = Integer.parseInt(experienceYearsStr);

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO Coach (coach_name, experience_years, team_id) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, coachName);
                pstmt.setInt(2, experienceYears);
                pstmt.setInt(3, teamId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Coach added successfully!");
                clearFields();
            } catch (SQLException e) {
                handleSQLException(e);
            } finally {
                closeResources(null, pstmt, conn);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
        }
    }

    private void handleSQLException(SQLException e) {
        if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
            JOptionPane.showMessageDialog(this, "Error: A coach for this team already exists.");
        } else {
            JOptionPane.showMessageDialog(this, "Error adding coach: " + e.getMessage());
        }
    }

    private void clearFields() {
        coachNameField.setText("");
        experienceYearsField.setText("");
        teamComboBox.setSelectedIndex(0);
    }

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
        AddCoachGUI addCoachGUI = new AddCoachGUI();
        addCoachGUI.setVisible(true);
    }
}
