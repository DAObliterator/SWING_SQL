package Venue;

import DBConnection.DBConnection; // Updated import statement
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

public class ViewVenuesGUI extends JFrame {
    private JTable venueTable;
    private DefaultTableModel tableModel;
    private JButton deleteSelectedButton, deleteAllButton;
    private JTextField filterField;
    private JComboBox<String> sortComboBox;

    public ViewVenuesGUI() {
        setTitle("View Venues");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table for displaying venues
        tableModel = new DefaultTableModel(new Object[]{"Venue ID", "Venue Name", "Location", "Capacity"}, 0);
        venueTable = new JTable(tableModel);

        // Add sorting functionality to the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        venueTable.setRowSorter(sorter);

        loadVenues();

        JScrollPane scrollPane = new JScrollPane(venueTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons for deleting venues
        JPanel buttonPanel = new JPanel();
        deleteSelectedButton = new JButton("Delete Selected");
        deleteAllButton = new JButton("Delete All");

        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(deleteAllButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sorting options
        JPanel sortPanel = new JPanel();
        sortPanel.add(new JLabel("Sort by:"));
        sortComboBox = new JComboBox<>(new String[]{"Venue ID", "Venue Name", "Location", "Capacity"});
        sortPanel.add(sortComboBox);
        add(sortPanel, BorderLayout.NORTH);

        // Filtering options
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter by Name/Location:"));
        filterField = new JTextField(10);
        filterPanel.add(filterField);
        JButton filterButton = new JButton("Apply Filter");
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.NORTH);

        // Action listener for deleting selected venues
        deleteSelectedButton.addActionListener(e -> deleteSelectedVenues());

        // Action listener for deleting all venues
        deleteAllButton.addActionListener(e -> deleteAllVenues());

        // Action listener for sorting
        sortComboBox.addActionListener(e -> sortVenues());

        // Action listener for applying filters
        filterButton.addActionListener(e -> applyFilter());
    }

    // Load venues into the table
    private void loadVenues() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT venue_id, venue_name, location, capacity FROM Venue";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int venueId = rs.getInt("venue_id");
                String venueName = rs.getString("venue_name");
                String location = rs.getString("location");
                int capacity = rs.getInt("capacity");

                tableModel.addRow(new Object[]{venueId, venueName, location, capacity});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, null);
        }
    }

    // Delete selected venues from the table and database
    private void deleteSelectedVenues() {
        int[] selectedRows = venueTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one venue to delete.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Venue WHERE venue_id = ?";

            for (int i : selectedRows) {
                int venueId = (int) venueTable.getValueAt(i, 0);
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, venueId);
                pstmt.executeUpdate();
            }

            // Refresh the table
            tableModel.setRowCount(0);
            loadVenues();
            JOptionPane.showMessageDialog(this, "Selected venues deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting selected venues: " + e.getMessage());
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    // Delete all venues from the table and database
    private void deleteAllVenues() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all venues?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.NO_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Venue";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            // Refresh the table
            tableModel.setRowCount(0);
            loadVenues();
            JOptionPane.showMessageDialog(this, "All venues deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting venues: " + e.getMessage());
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    // Sort venues based on the selected option
    private void sortVenues() {
        int columnIndex = sortComboBox.getSelectedIndex();
        venueTable.getRowSorter().toggleSortOrder(columnIndex);
    }

    // Apply filter to the table
    private void applyFilter() {
        String filterText = filterField.getText();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) venueTable.getRowSorter();

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
            ViewVenuesGUI viewVenuesGUI = new ViewVenuesGUI();
            viewVenuesGUI.setVisible(true);
        });
    }
}
