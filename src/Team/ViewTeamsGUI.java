package Team;

import DBConnection.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewTeamsGUI extends JFrame {
    private JTable teamTable;
    private DefaultTableModel tableModel;
    private JButton deleteSelectedButton, deleteAllButton;
    private JTextField filterField;
    private JComboBox<String> sortComboBox;

    public ViewTeamsGUI() {
        setTitle("View Teams");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table for displaying teams
        tableModel = new DefaultTableModel(new Object[]{"Team ID", "Team Name", "City", "Owner", "Home Venue ID"}, 0);
        teamTable = new JTable(tableModel);

        // Add sorting functionality to the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        teamTable.setRowSorter(sorter);

        loadTeams();

        JScrollPane scrollPane = new JScrollPane(teamTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons for deleting teams
        JPanel buttonPanel = new JPanel();
        deleteSelectedButton = new JButton("Delete Selected");
        deleteAllButton = new JButton("Delete All");
        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(deleteAllButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sorting options
        JPanel sortPanel = new JPanel();
        sortPanel.add(new JLabel("Sort by:"));
        sortComboBox = new JComboBox<>(new String[]{"Team ID", "Team Name", "City", "Owner", "Home Venue ID"});
        sortPanel.add(sortComboBox);
        add(sortPanel, BorderLayout.NORTH);

        // Filtering options
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter by Name/City:"));
        filterField = new JTextField(10);
        filterPanel.add(filterField);
        JButton filterButton = new JButton("Apply Filter");
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.NORTH);

        // Action listener for deleting selected teams
        deleteSelectedButton.addActionListener(e -> deleteSelectedTeams());

        // Action listener for deleting all teams
        deleteAllButton.addActionListener(e -> deleteAllTeams());

        // Action listener for sorting
        sortComboBox.addActionListener(e -> sortTeams());

        // Action listener for applying filters
        filterButton.addActionListener(e -> applyFilter());
    }

    // Load teams into the table
    private void loadTeams() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT team_id, team_name, city, owner, home_venue_id FROM Team";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int teamId = rs.getInt("team_id");
                String teamName = rs.getString("team_name");
                String city = rs.getString("city");
                String owner = rs.getString("owner");
                int homeVenueId = rs.getInt("home_venue_id");

                tableModel.addRow(new Object[]{teamId, teamName, city, owner, homeVenueId});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, null);
        }
    }

    // Delete selected teams and set their players' team_id to NULL
    private void deleteSelectedTeams() {
        int[] selectedRows = teamTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one team to delete.");
            return;
        }

        Connection conn = null;
        PreparedStatement deleteTeamStmt = null;
        PreparedStatement updatePlayerStmt = null;

        try {
            conn = DBConnection.getConnection();
            String deleteTeamSQL = "DELETE FROM Team WHERE team_id = ?";
            String updatePlayerSQL = "UPDATE Player SET team_id = NULL WHERE team_id = ?";

            for (int i : selectedRows) {
                int teamId = (int) teamTable.getValueAt(i, 0);

                updatePlayerStmt = conn.prepareStatement(updatePlayerSQL);
                updatePlayerStmt.setInt(1, teamId);
                updatePlayerStmt.executeUpdate();

                deleteTeamStmt = conn.prepareStatement(deleteTeamSQL);
                deleteTeamStmt.setInt(1, teamId);
                deleteTeamStmt.executeUpdate();
            }

            tableModel.setRowCount(0); // Clear the table model
            loadTeams(); // Reload the updated data
            JOptionPane.showMessageDialog(this, "Selected teams deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting selected teams: " + e.getMessage());
        } finally {
            closeResources(null, deleteTeamStmt, conn);
            closeResources(null, updatePlayerStmt, null);
        }
    }

    // Delete all teams and set their players' team_id to NULL
    private void deleteAllTeams() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all teams?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.NO_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement deleteTeamsStmt = null;
        PreparedStatement updatePlayersStmt = null;

        try {
            conn = DBConnection.getConnection();

            String updatePlayersSQL = "UPDATE Player SET team_id = NULL";
            updatePlayersStmt = conn.prepareStatement(updatePlayersSQL);
            updatePlayersStmt.executeUpdate();

            String deleteTeamsSQL = "DELETE FROM Team";
            deleteTeamsStmt = conn.prepareStatement(deleteTeamsSQL);
            deleteTeamsStmt.executeUpdate();

            tableModel.setRowCount(0); // Clear the table model
            loadTeams(); // Reload the updated data
            JOptionPane.showMessageDialog(this, "All teams deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting teams: " + e.getMessage());
        } finally {
            closeResources(null, deleteTeamsStmt, conn);
            closeResources(null, updatePlayersStmt, null);
        }
    }

    // Sort teams based on the selected option
    private void sortTeams() {
        int columnIndex = sortComboBox.getSelectedIndex();
        teamTable.getRowSorter().toggleSortOrder(columnIndex);
    }

    // Apply filter to the table
    private void applyFilter() {
        String filterText = filterField.getText();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) teamTable.getRowSorter();

        // Create a RowFilter based on the input
        RowFilter<DefaultTableModel, Object> filter;
        if (filterText.trim().isEmpty()) {
            filter = null; // No filter if the field is empty
        } else {
            filter = RowFilter.regexFilter("(?i)" + filterText); // Case-insensitive filter
        }
        sorter.setRowFilter(filter);
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
            ViewTeamsGUI viewTeamsGUI = new ViewTeamsGUI();
            viewTeamsGUI.setVisible(true);
        });
    }
}
