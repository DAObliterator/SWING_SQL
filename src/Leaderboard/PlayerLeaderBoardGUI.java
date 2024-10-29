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

public class PlayerLeaderBoardGUI extends JFrame {
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private JTextField filterField;

    public PlayerLeaderBoardGUI() {
        setTitle("Player Leaderboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the table with appropriate column types
        tableModel = new DefaultTableModel(new Object[]{"Player ID", "Player Name", "Points", "Assists", "Rebounds"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Return class types for sorting
                switch (columnIndex) {
                    case 0: return Integer.class; // Player ID
                    case 1: return String.class;   // Player Name
                    case 2: return Integer.class;   // Points
                    case 3: return Integer.class;   // Assists
                    case 4: return Integer.class;   // Rebounds
                    default: return String.class;
                }
            }
        };

        leaderboardTable = new JTable(tableModel);
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
        filterPanel.add(new JLabel("Filter by Player Name:"));
        filterPanel.add(filterField);
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.NORTH);

        filterButton.addActionListener(e -> applyFilter(sorter));
    }

    private void loadLeaderBoard() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT p.player_id, CONCAT(p.first_name, ' ', p.last_name) AS player_name, " +
                             "COALESCE(SUM(s.points), 0) AS total_points, " +
                             "COALESCE(SUM(s.assists), 0) AS total_assists, " +
                             "COALESCE(SUM(s.rebounds), 0) AS total_rebounds " +
                             "FROM Player p " +
                             "LEFT JOIN Stats s ON p.player_id = s.player_id " +
                             "GROUP BY p.player_id " +
                             "ORDER BY total_points DESC")) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String playerName = rs.getString("player_name");
                int totalPoints = rs.getInt("total_points");
                int totalAssists = rs.getInt("total_assists");
                int totalRebounds = rs.getInt("total_rebounds");

                tableModel.addRow(new Object[]{playerId, playerName, totalPoints, totalAssists, totalRebounds});
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
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText, 1)); // Filter by Player Name (column index 1)
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlayerLeaderBoardGUI leaderboardGUI = new PlayerLeaderBoardGUI();
            leaderboardGUI.setVisible(true);
        });
    }
}
