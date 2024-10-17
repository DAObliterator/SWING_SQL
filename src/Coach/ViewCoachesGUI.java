package Coach;

import DBConnection.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewCoachesGUI extends JFrame {
    private JTable coachTable;
    private DefaultTableModel tableModel;
    private JButton deleteSelectedButton, deleteAllButton;

    public ViewCoachesGUI() {
        setTitle("View Coaches");
        setSize(600, 500); // Adjusted size for sorting and filtering components
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table for displaying coaches
        tableModel = new DefaultTableModel(new Object[]{"Coach ID", "Coach Name", "Experience Years", "Team ID"}, 0);
        coachTable = new JTable(tableModel);
        loadCoaches();

        JScrollPane scrollPane = new JScrollPane(coachTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons for deleting coaches
        JPanel buttonPanel = new JPanel();
        deleteSelectedButton = new JButton("Delete Selected");
        deleteAllButton = new JButton("Delete All");

        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(deleteAllButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sorting and filtering components
        JPanel filterSortPanel = new JPanel();
        JComboBox<String> sortOptions = new JComboBox<>(new String[]{"Sort by", "Name", "Experience", "Team"});
        JTextField nameFilterField = new JTextField(10);
        JButton filterButton = new JButton("Filter");

        // Add sorting and filtering to panel
        filterSortPanel.add(new JLabel("Sort by:"));
        filterSortPanel.add(sortOptions);
        filterSortPanel.add(new JLabel("Filter by Name:"));
        filterSortPanel.add(nameFilterField);
        filterSortPanel.add(filterButton);

        add(filterSortPanel, BorderLayout.NORTH); // Add above the table

        // Action listener for deleting selected coaches
        deleteSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedCoaches();
            }
        });

        // Action listener for deleting all coaches
        deleteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAllCoaches();
            }
        });

        // Action listener for sorting
        sortOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSort = (String) sortOptions.getSelectedItem();
                if (selectedSort.equals("Name")) {
                    sortByName();
                } else if (selectedSort.equals("Experience")) {
                    sortByExperience();
                } else if (selectedSort.equals("Team")) {
                    sortByTeam();
                }
            }
        });

        // Action listener for filtering
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameFilter = nameFilterField.getText();
                filterByName(nameFilter);
            }
        });
    }

    // Load coaches into the table
    private void loadCoaches() {
        tableModel.setRowCount(0); // Clear the table before loading data
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT coach_id, coach_name, experience_years, team_id FROM Coach";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int coachId = rs.getInt("coach_id");
                String coachName = rs.getString("coach_name");
                int experienceYears = rs.getInt("experience_years");
                int teamId = rs.getInt("team_id");

                tableModel.addRow(new Object[]{coachId, coachName, experienceYears, teamId});
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

    // Sorting by name
    private void sortByName() {
        tableModel.setRowCount(0); // Clear table
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT coach_id, coach_name, experience_years, team_id FROM Coach ORDER BY coach_name ASC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int coachId = rs.getInt("coach_id");
                String coachName = rs.getString("coach_name");
                int experienceYears = rs.getInt("experience_years");
                int teamId = rs.getInt("team_id");

                tableModel.addRow(new Object[]{coachId, coachName, experienceYears, teamId});
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

    // Sorting by experience
    private void sortByExperience() {
        tableModel.setRowCount(0); // Clear table
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT coach_id, coach_name, experience_years, team_id FROM Coach ORDER BY experience_years DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int coachId = rs.getInt("coach_id");
                String coachName = rs.getString("coach_name");
                int experienceYears = rs.getInt("experience_years");
                int teamId = rs.getInt("team_id");

                tableModel.addRow(new Object[]{coachId, coachName, experienceYears, teamId});
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

    // Sorting by team
    private void sortByTeam() {
        tableModel.setRowCount(0); // Clear table
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT coach_id, coach_name, experience_years, team_id FROM Coach ORDER BY team_id ASC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int coachId = rs.getInt("coach_id");
                String coachName = rs.getString("coach_name");
                int experienceYears = rs.getInt("experience_years");
                int teamId = rs.getInt("team_id");

                tableModel.addRow(new Object[]{coachId, coachName, experienceYears, teamId});
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

    // Filtering by name
    private void filterByName(String nameFilter) {
        tableModel.setRowCount(0); // Clear table
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT coach_id, coach_name, experience_years, team_id FROM Coach WHERE coach_name LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + nameFilter + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int coachId = rs.getInt("coach_id");
                String coachName = rs.getString("coach_name");
                int experienceYears = rs.getInt("experience_years");
                int teamId = rs.getInt("team_id");

                tableModel.addRow(new Object[]{coachId, coachName, experienceYears, teamId});
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

    // Delete selected coaches from the table and database
    private void deleteSelectedCoaches() {
        int[] selectedRows = coachTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one coach to delete.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Coach WHERE coach_id = ?";

            for (int i : selectedRows) {
                int coachId = (int) coachTable.getValueAt(i, 0);
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, coachId);
                pstmt.executeUpdate();
            }

            // Refresh the table
            tableModel.setRowCount(0);
            loadCoaches();
            JOptionPane.showMessageDialog(this, "Selected coaches deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting selected coaches: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete all coaches from the table and database
    private void deleteAllCoaches() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all coaches?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.NO_OPTION) {
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Coach";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            // Refresh the table
            tableModel.setRowCount(0);
            loadCoaches();
            JOptionPane.showMessageDialog(this, "All coaches deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting coaches: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ViewCoachesGUI viewCoachesGUI = new ViewCoachesGUI();
        viewCoachesGUI.setVisible(true);
    }
}
