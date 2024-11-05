package Leaderboard;

import DBConnection.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamLeaderBoardGUI extends JFrame {
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private JTextField filterField;

    public TeamLeaderBoardGUI() {
        setTitle("Team Leaderboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the table
        tableModel = new DefaultTableModel(new String[]{"Team ID", "Team Name", "Total Points", "Wins", "Losses"}, 0);
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFillsViewportHeight(true);
        loadLeaderBoard();

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        leaderboardTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        add(scrollPane, BorderLayout.CENTER);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterField = new JTextField(15);
        JButton filterButton = new JButton("Filter");
        filterPanel.add(new JLabel("Filter by Team Name:"));
        filterPanel.add(filterField);
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.NORTH);

        filterButton.addActionListener(e -> applyFilter(sorter));
    }

    private void loadLeaderBoard() {
        try (

                Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT t.team_id, t.team_name, " +
                                "COUNT(m.match_id) AS total_matches, " +
                                "COUNT(CASE WHEN m.winner_team_id = t.team_id THEN 1 END) AS wins, " +
                                "COUNT(CASE WHEN m.winner_team_id IS NULL OR m.winner_team_id != t.team_id THEN 1 END) AS losses, " +
                                "(COUNT(CASE WHEN m.winner_team_id = t.team_id THEN 1 END) * 2) AS total_points " +
                                "FROM Team t " +
                                "LEFT JOIN `Match` m ON t.team_id = m.home_team_id OR t.team_id = m.away_team_id " +
                                "GROUP BY t.team_id, t.team_name"
                );

        ) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int teamId = rs.getInt("team_id");
                String teamName = rs.getString("team_name");
                int totalPoints = rs.getInt("total_points");
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");

                tableModel.addRow(new Object[]{teamId, teamName, totalPoints, wins, losses});
            }
        } catch (SQLException e) {
            showError("Error loading leaderboard: " + e.getMessage());
        }
    }

    private void applyFilter(TableRowSorter<DefaultTableModel> sorter) {
        String filterText = filterField.getText();
        if (filterText.trim().isEmpty()) {
            sorter.setRowFilter(null); // No filter if the field is empty
        } else {
            // Use regex for case-insensitive filtering
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText, 1)); // Filter by Team Name (column index 1)
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TeamLeaderBoardGUI leaderboardGUI = new TeamLeaderBoardGUI();
            leaderboardGUI.setVisible(true);
        });
    }
}
