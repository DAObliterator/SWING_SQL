package Referee;

import DBConnection.DBConnection; // Update this line
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

public class ViewRefereesGUI extends JFrame {
    private JTable refereeTable;
    private DefaultTableModel tableModel;
    private JButton deleteSelectedButton, deleteAllButton;
    private JComboBox<String> sortComboBox;
    private JTextField filterNameField;

    public ViewRefereesGUI() {
        setTitle("View Referees");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table for displaying referees
        tableModel = new DefaultTableModel(new Object[]{"Referee ID", "Referee Name", "Experience Years"}, 0);
        refereeTable = new JTable(tableModel);

        // Add sorting functionality to table columns
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        refereeTable.setRowSorter(sorter);

        loadReferees();

        JScrollPane scrollPane = new JScrollPane(refereeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons for deleting referees
        JPanel buttonPanel = new JPanel();
        deleteSelectedButton = new JButton("Delete Selected");
        deleteAllButton = new JButton("Delete All");

        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(deleteAllButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sorting options
        JPanel sortPanel = new JPanel();
        sortPanel.add(new JLabel("Sort by:"));
        sortComboBox = new JComboBox<>(new String[]{"Referee ID", "Referee Name", "Experience Years"});
        sortPanel.add(sortComboBox);
        add(sortPanel, BorderLayout.NORTH);

        // Filtering options
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter by Name:"));
        filterNameField = new JTextField(10);
        filterPanel.add(filterNameField);
        JButton filterButton = new JButton("Apply Filter");
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.NORTH);

        // Action listener for deleting selected referees
        deleteSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedReferees();
            }
        });

        // Action listener for deleting all referees
        deleteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAllReferees();
            }
        });

        // Action listener for sorting
        sortComboBox.addActionListener(e -> sortReferees());

        // Action listener for applying filters
        filterButton.addActionListener(e -> applyFilter());
    }

    // Load referees into the table
    private void loadReferees() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT referee_id, referee_name, experience_years FROM Referee";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int refereeId = rs.getInt("referee_id");
                String refereeName = rs.getString("referee_name");
                int experienceYears = rs.getInt("experience_years");

                tableModel.addRow(new Object[]{refereeId, refereeName, experienceYears});
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

    // Delete selected referees from the table and database
    private void deleteSelectedReferees() {
        int[] selectedRows = refereeTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one referee to delete.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Referee WHERE referee_id = ?";

            for (int i : selectedRows) {
                int refereeId = (int) refereeTable.getValueAt(i, 0);
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, refereeId);
                pstmt.executeUpdate();
            }

            // Refresh the table
            tableModel.setRowCount(0);
            loadReferees();
            JOptionPane.showMessageDialog(this, "Selected referees deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting selected referees: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete all referees from the table and database
    private void deleteAllReferees() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all referees?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.NO_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Referee";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            // Refresh the table
            tableModel.setRowCount(0);
            loadReferees();
            JOptionPane.showMessageDialog(this, "All referees deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting referees: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Sort referees based on selected option
    private void sortReferees() {
        int columnIndex = sortComboBox.getSelectedIndex();
        refereeTable.getRowSorter().toggleSortOrder(columnIndex);
    }

    // Apply filter to the referee table based on name
    private void applyFilter() {
        String nameFilter = filterNameField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) refereeTable.getRowSorter();
        RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter(nameFilter, 1); // Column 1 for Referee Name
        sorter.setRowFilter(filter);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewRefereesGUI viewRefereesGUI = new ViewRefereesGUI();
            viewRefereesGUI.setVisible(true);
        });
    }
}
