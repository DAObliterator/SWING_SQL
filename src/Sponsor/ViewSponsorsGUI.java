package Sponsor;

import DBConnection.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewSponsorsGUI extends JFrame {
    private JTable sponsorTable;
    private DefaultTableModel tableModel;
    private JButton deleteSelectedButton, deleteAllButton;
    private JComboBox<String> sortComboBox;
    private JTextField filterNameField;

    public ViewSponsorsGUI() {
        setTitle("View Sponsors");
        setSize(600, 400); // Adjusted size for better table display
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table for displaying sponsors
        tableModel = new DefaultTableModel(new Object[]{"Sponsor ID", "Sponsor Name", "Team ID", "Sponsor Value"}, 0);
        sponsorTable = new JTable(tableModel);

        // Add sorting functionality to the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sponsorTable.setRowSorter(sorter);

        loadSponsors(); // Load sponsors from DB

        JScrollPane scrollPane = new JScrollPane(sponsorTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons for deleting sponsors
        JPanel buttonPanel = new JPanel();
        deleteSelectedButton = new JButton("Delete Selected");
        deleteAllButton = new JButton("Delete All");
        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(deleteAllButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Panel for sorting and filtering
        JPanel topPanel = new JPanel(new BorderLayout());

        // Sorting options
        JPanel sortPanel = new JPanel();
        sortPanel.add(new JLabel("Sort by:"));
        sortComboBox = new JComboBox<>(new String[]{"Sponsor ID", "Sponsor Name", "Team ID", "Sponsor Value"});
        sortPanel.add(sortComboBox);
        topPanel.add(sortPanel, BorderLayout.WEST);

        // Filtering options
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter by Name:"));
        filterNameField = new JTextField(10);
        filterPanel.add(filterNameField);
        JButton filterButton = new JButton("Apply Filter");
        filterPanel.add(filterButton);
        topPanel.add(filterPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH); // Add top panel to NORTH region

        // Action listener for deleting selected sponsors
        deleteSelectedButton.addActionListener(e -> deleteSelectedSponsors());

        // Action listener for deleting all sponsors
        deleteAllButton.addActionListener(e -> deleteAllSponsors());

        // Action listener for sorting
        sortComboBox.addActionListener(e -> sortSponsors());

        // Action listener for applying filters
        filterButton.addActionListener(e -> applyFilter());
    }

    // Load sponsors into the table
    private void loadSponsors() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT sponsor_id, sponsor_name, team_id, sponsor_value FROM Sponsor";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int sponsorId = rs.getInt("sponsor_id");
                String sponsorName = rs.getString("sponsor_name");
                int teamId = rs.getInt("team_id");
                BigDecimal sponsorValue = rs.getBigDecimal("sponsor_value");

                tableModel.addRow(new Object[]{sponsorId, sponsorName, teamId, sponsorValue});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    // Delete selected sponsors from the table and database
    private void deleteSelectedSponsors() {
        int[] selectedRows = sponsorTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one sponsor to delete.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Sponsor WHERE sponsor_id = ?";

            for (int i : selectedRows) {
                int sponsorId = (int) sponsorTable.getValueAt(i, 0);
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, sponsorId);
                pstmt.executeUpdate();
            }

            // Refresh the table
            tableModel.setRowCount(0);
            loadSponsors();
            JOptionPane.showMessageDialog(this, "Selected sponsors deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting selected sponsors: " + e.getMessage());
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    // Delete all sponsors from the table and database
    private void deleteAllSponsors() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all sponsors?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM Sponsor";
                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();

                // Refresh the table
                tableModel.setRowCount(0);
                loadSponsors();
                JOptionPane.showMessageDialog(this, "All sponsors deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting all sponsors: " + e.getMessage());
            } finally {
                closeResources(null, pstmt, conn);
            }
        }
    }

    // Sort sponsors based on selected option
    private void sortSponsors() {
        int columnIndex = sortComboBox.getSelectedIndex();
        sponsorTable.getRowSorter().toggleSortOrder(columnIndex);
    }

    // Apply filter to the sponsor table based on name
    private void applyFilter() {
        String nameFilter = filterNameField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) sponsorTable.getRowSorter();
        RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter("(?i)" + nameFilter, 1); // Column 1 for Sponsor Name
        sorter.setRowFilter(filter);
    }

    // Close main.resources to avoid resource leaks
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
            ViewSponsorsGUI viewSponsorsGUI = new ViewSponsorsGUI();
            viewSponsorsGUI.setVisible(true);
        });
    }
}
