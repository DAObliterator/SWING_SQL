package Match;

import DBConnection.DBConnection;
import Player.UpdatePlayerGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewMatchGUI extends JFrame {
    private JTable matchTable;
    private DefaultTableModel tableModel;

    public ViewMatchGUI() {
        setTitle("View Matches");
        setSize(700, 500); // Adjusted size for sorting and filtering components
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Match ID", "Home Team", "Away Team", "Match Date", "Venue", "Referee", "Winner Team"}, 0);
        matchTable = new JTable(tableModel);
        loadMatches();

        JScrollPane scrollPane = new JScrollPane(matchTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton deleteSelectedButton = new JButton("Delete Selected Match");
        JButton deleteAllButton = new JButton("Delete All Matches");
        JButton updateSelectedButton = new JButton("Update Selected Match ");
        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(deleteAllButton);
        buttonPanel.add(updateSelectedButton);


        // Sorting and filtering components
        JPanel filterSortPanel = new JPanel();
        JComboBox<String> sortOptions = new JComboBox<>(new String[]{"Sort by", "Date", "Winner Team"});
        JTextField dateFilterField = new JTextField(10);
        JButton filterByDateButton = new JButton("Filter by Date");
        JButton filterByTeamButton = new JButton("Filter by Team");

        // Add components to the filterSortPanel
        filterSortPanel.add(new JLabel("Sort by:"));
        filterSortPanel.add(sortOptions);
        filterSortPanel.add(new JLabel("Filter by Date (YYYY-MM-DD):"));
        filterSortPanel.add(dateFilterField);
        filterSortPanel.add(filterByDateButton);
        filterSortPanel.add(filterByTeamButton);



        //combinedPanel
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(filterSortPanel , BorderLayout.SOUTH);
        combinedPanel.add(buttonPanel , BorderLayout.NORTH);
        add(combinedPanel , BorderLayout.SOUTH);


        deleteSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedMatch();
            }
        });

        deleteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAllMatches();
            }
        });

        updateSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedMatch();
            }
        });

        // Action listener for sorting
        sortOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSort = (String) sortOptions.getSelectedItem();
                if (selectedSort.equals("Date")) {
                    sortByDate();
                } else if (selectedSort.equals("Winner Team")) {
                    sortByWinnerTeam();
                }
            }
        });

        // Action listener for filtering by date
        filterByDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dateFilter = dateFilterField.getText();
                filterByDate(dateFilter);
            }
        });

        // Action listener for filtering by team
        filterByTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String teamName = JOptionPane.showInputDialog("Enter Team Name:");
                if (teamName != null) {
                    filterByTeam(teamName);
                }
            }
        });
    }

    private void loadMatches() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT m.match_id, t1.team_name AS home_team, t2.team_name AS away_team, m.match_date, v.venue_name, r.referee_name, t3.team_name AS winner_team " +
                    "FROM `Match` m " +
                    "JOIN Team t1 ON m.home_team_id = t1.team_id " +
                    "JOIN Team t2 ON m.away_team_id = t2.team_id " +
                    "JOIN Venue v ON m.venue_id = v.venue_id " +
                    "JOIN Referee r ON m.referee_id = r.referee_id " +
                    "LEFT JOIN Team t3 ON m.winner_team_id = t3.team_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int matchId = rs.getInt("match_id");
                String homeTeam = rs.getString("home_team");
                String awayTeam = rs.getString("away_team");
                String matchDate = rs.getDate("match_date").toString();
                String venue = rs.getString("venue_name");
                String referee = rs.getString("referee_name");
                String winnerTeam = rs.getString("winner_team");

                if (winnerTeam == null) {
                    winnerTeam = "N/A";
                }

                tableModel.addRow(new Object[]{matchId, homeTeam, awayTeam, matchDate, venue, referee, winnerTeam});
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

    private void deleteSelectedMatch() {
        int selectedRow = matchTable.getSelectedRow();
        if (selectedRow >= 0) {
            int matchId = (Integer) tableModel.getValueAt(selectedRow, 0);
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM `Match` WHERE match_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, matchId);
                pstmt.executeUpdate();

                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Match deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting match: " + e.getMessage());
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a match to delete.");
        }
    }

    private void deleteAllMatches() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all matches?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM `Match`";
                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();

                tableModel.setRowCount(0);
                JOptionPane.showMessageDialog(this, "All matches deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting all matches: " + e.getMessage());
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sortByDate() {
        tableModel.setRowCount(0); // Clear the table
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT m.match_id, t1.team_name AS home_team, t2.team_name AS away_team, m.match_date, v.venue_name, r.referee_name, t3.team_name AS winner_team " +
                    "FROM `Match` m " +
                    "JOIN Team t1 ON m.home_team_id = t1.team_id " +
                    "JOIN Team t2 ON m.away_team_id = t2.team_id " +
                    "JOIN Venue v ON m.venue_id = v.venue_id " +
                    "JOIN Referee r ON m.referee_id = r.referee_id " +
                    "LEFT JOIN Team t3 ON m.winner_team_id = t3.team_id " +
                    "ORDER BY m.match_date ASC"; // Order by match date
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int matchId = rs.getInt("match_id");
                String homeTeam = rs.getString("home_team");
                String awayTeam = rs.getString("away_team");
                String matchDate = rs.getDate("match_date").toString();
                String venue = rs.getString("venue_name");
                String referee = rs.getString("referee_name");
                String winnerTeam = rs.getString("winner_team");

                if (winnerTeam == null) {
                    winnerTeam = "N/A";
                }

                tableModel.addRow(new Object[]{matchId, homeTeam, awayTeam, matchDate, venue, referee, winnerTeam});
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

    private void sortByWinnerTeam() {
        tableModel.setRowCount(0); // Clear the table
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT m.match_id, t1.team_name AS home_team, t2.team_name AS away_team, m.match_date, v.venue_name, r.referee_name, t3.team_name AS winner_team " +
                    "FROM `Match` m " +
                    "JOIN Team t1 ON m.home_team_id = t1.team_id " +
                    "JOIN Team t2 ON m.away_team_id = t2.team_id " +
                    "JOIN Venue v ON m.venue_id = v.venue_id " +
                    "JOIN Referee r ON m.referee_id = r.referee_id " +
                    "LEFT JOIN Team t3 ON m.winner_team_id = t3.team_id " +
                    "ORDER BY t3.team_name ASC"; // Order by winner team
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int matchId = rs.getInt("match_id");
                String homeTeam = rs.getString("home_team");
                String awayTeam = rs.getString("away_team");
                String matchDate = rs.getDate("match_date").toString();
                String venue = rs.getString("venue_name");
                String referee = rs.getString("referee_name");
                String winnerTeam = rs.getString("winner_team");

                if (winnerTeam == null) {
                    winnerTeam = "N/A";
                }

                tableModel.addRow(new Object[]{matchId, homeTeam, awayTeam, matchDate, venue, referee, winnerTeam});
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

    private void filterByDate(String date) {
        tableModel.setRowCount(0); // Clear the table
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT m.match_id, t1.team_name AS home_team, t2.team_name AS away_team, m.match_date, v.venue_name, r.referee_name, t3.team_name AS winner_team " +
                    "FROM `Match` m " +
                    "JOIN Team t1 ON m.home_team_id = t1.team_id " +
                    "JOIN Team t2 ON m.away_team_id = t2.team_id " +
                    "JOIN Venue v ON m.venue_id = v.venue_id " +
                    "JOIN Referee r ON m.referee_id = r.referee_id " +
                    "LEFT JOIN Team t3 ON m.winner_team_id = t3.team_id " +
                    "WHERE m.match_date = ?"; // Filter by match date
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, date);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int matchId = rs.getInt("match_id");
                String homeTeam = rs.getString("home_team");
                String awayTeam = rs.getString("away_team");
                String matchDate = rs.getDate("match_date").toString();
                String venue = rs.getString("venue_name");
                String referee = rs.getString("referee_name");
                String winnerTeam = rs.getString("winner_team");

                if (winnerTeam == null) {
                    winnerTeam = "N/A";
                }

                tableModel.addRow(new Object[]{matchId, homeTeam, awayTeam, matchDate, venue, referee, winnerTeam});
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

    private void filterByTeam(String teamName) {
        tableModel.setRowCount(0); // Clear the table
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT m.match_id, t1.team_name AS home_team, t2.team_name AS away_team, m.match_date, v.venue_name, r.referee_name, t3.team_name AS winner_team " +
                    "FROM `Match` m " +
                    "JOIN Team t1 ON m.home_team_id = t1.team_id " +
                    "JOIN Team t2 ON m.away_team_id = t2.team_id " +
                    "JOIN Venue v ON m.venue_id = v.venue_id " +
                    "JOIN Referee r ON m.referee_id = r.referee_id " +
                    "LEFT JOIN Team t3 ON m.winner_team_id = t3.team_id " +
                    "WHERE t1.team_name LIKE ? OR t2.team_name LIKE ?"; // Filter by home or away team
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + teamName + "%");
            pstmt.setString(2, "%" + teamName + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int matchId = rs.getInt("match_id");
                String homeTeam = rs.getString("home_team");
                String awayTeam = rs.getString("away_team");
                String matchDate = rs.getDate("match_date").toString();
                String venue = rs.getString("venue_name");
                String referee = rs.getString("referee_name");
                String winnerTeam = rs.getString("winner_team");

                if (winnerTeam == null) {
                    winnerTeam = "N/A";
                }

                tableModel.addRow(new Object[]{matchId, homeTeam, awayTeam, matchDate, venue, referee, winnerTeam});
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

    private void updateSelectedMatch() {

        int selectedRow = matchTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the actual row index in the model in case the table is sorted
            int modelRow = matchTable.convertRowIndexToModel(selectedRow);

            // Get player ID from the selected row in the table model
            int matchId = (int) tableModel.getValueAt(modelRow, 0);

            // Pass playerId to UpdatePlayerGUI
            UpdateMatchGUI updateMatchGUI = new UpdateMatchGUI(matchId);
            updateMatchGUI.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a player to update.");
        }

    }

    public static void main(String[] args) {
        ViewMatchGUI viewMatchGUI = new ViewMatchGUI();
        viewMatchGUI.setVisible(true);
    }
}
