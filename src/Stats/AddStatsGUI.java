package Stats;

import DBConnection.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddStatsGUI extends JFrame {
    private JComboBox<String> playerComboBox;
    private JComboBox<String> matchComboBox;
    private JTextField pointsField;
    private JTextField assistsField;
    private JTextField reboundsField;

    public AddStatsGUI() {
        setTitle("Add Stats");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        playerComboBox = new JComboBox<>();
        matchComboBox = new JComboBox<>();
        pointsField = new JTextField();
        assistsField = new JTextField();
        reboundsField = new JTextField();

        add(new JLabel("Select Player:"));
        add(playerComboBox);
        add(new JLabel("Select Match:"));
        add(matchComboBox);
        add(new JLabel("Points:"));
        add(pointsField);
        add(new JLabel("Assists:"));
        add(assistsField);
        add(new JLabel("Rebounds:"));
        add(reboundsField);

        JButton addButton = new JButton("Add Stats");
        add(addButton);

        // Load players and matches into the combo boxes
        loadPlayers();
        loadMatches();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStats();
            }
        });
    }

    private void loadPlayers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT player_id, player_name FROM Player";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String playerName = rs.getString("player_name");
                playerComboBox.addItem(playerId + " - " + playerName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    private void loadMatches() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT match_id, home_team_id, away_team_id, match_date FROM `Match`";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int matchId = rs.getInt("match_id");
                String homeTeamId = String.valueOf(rs.getInt("home_team_id"));
                String awayTeamId = String.valueOf(rs.getInt("away_team_id"));
                String matchDate = rs.getDate("match_date").toString();
                matchComboBox.addItem(matchId + " (Home: " + homeTeamId + ", Away: " + awayTeamId + ", Date: " + matchDate + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    private void addStats() {
        String selectedPlayer = (String) playerComboBox.getSelectedItem();
        String selectedMatch = (String) matchComboBox.getSelectedItem();

        if (selectedPlayer != null && selectedMatch != null) {
            int playerId = Integer.parseInt(selectedPlayer.split(" - ")[0]);
            int matchId = Integer.parseInt(selectedMatch.split(" ")[0]);
            int points = Integer.parseInt(pointsField.getText());
            int assists = Integer.parseInt(assistsField.getText());
            int rebounds = Integer.parseInt(reboundsField.getText());

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO Stats (player_id, match_id, points, assists, rebounds) VALUES (?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, playerId);
                pstmt.setInt(2, matchId);
                pstmt.setInt(3, points);
                pstmt.setInt(4, assists);
                pstmt.setInt(5, rebounds);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Stats added successfully!");
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding stats: " + e.getMessage());
            } finally {
                closeResources(null, pstmt, conn);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select both a player and a match.");
        }
    }

    private void clearFields() {
        pointsField.setText("");
        assistsField.setText("");
        reboundsField.setText("");
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
        AddStatsGUI addStatsGUI = new AddStatsGUI();
        addStatsGUI.setVisible(true);
    }
}
