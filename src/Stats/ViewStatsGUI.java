package Stats;

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

public class ViewStatsGUI extends JFrame {
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JTextField filterField;
    private JComboBox<String> sortComboBox;

    public ViewStatsGUI() {
        setTitle("View Stats");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Player ID", "Match ID", "Points", "Assists", "Rebounds"}, 0);
        statsTable = new JTable(tableModel);

        // Add sorting functionality to table columns
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        statsTable.setRowSorter(sorter);

        loadStats();

        JScrollPane scrollPane = new JScrollPane(statsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton deleteSelectedButton = new JButton("Delete Selected Stat");
        JButton deleteAllButton = new JButton("Delete All Stats");
        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(deleteAllButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sorting options
        JPanel sortPanel = new JPanel();
        sortPanel.add(new JLabel("Sort by:"));
        sortComboBox = new JComboBox<>(new String[]{"Player ID", "Match ID", "Points", "Assists", "Rebounds"});
        sortPanel.add(sortComboBox);
        add(sortPanel, BorderLayout.NORTH);

        // Filtering options
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter by Player ID:"));
        filterField = new JTextField(10);
        filterPanel.add(filterField);
        JButton filterButton = new JButton("Apply Filter");
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.NORTH);

        deleteSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedStat();
            }
        });

        deleteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAllStats();
            }
        });

        // Action listener for sorting
        sortComboBox.addActionListener(e -> sortStats());

        // Action listener for applying filters
        filterButton.addActionListener(e -> applyFilter());
    }

    private void loadStats() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT player_id, match_id, points, assists, rebounds FROM Stats";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                int matchId = rs.getInt("match_id");
                int points = rs.getInt("points");
                int assists = rs.getInt("assists");
                int rebounds = rs.getInt("rebounds");

                tableModel.addRow(new Object[]{playerId, matchId, points, assists, rebounds});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    private void deleteSelectedStat() {
        int selectedRow = statsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int playerId = (Integer) tableModel.getValueAt(selectedRow, 0);
            int matchId = (Integer) tableModel.getValueAt(selectedRow, 1);
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM Stats WHERE player_id = ? AND match_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, playerId);
                pstmt.setInt(2, matchId);
                pstmt.executeUpdate();

                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Stat deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting stat: " + e.getMessage());
            } finally {
                closeResources(null, pstmt, conn);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a stat to delete.");
        }
    }

    private void deleteAllStats() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all stats?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM Stats";
                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();

                tableModel.setRowCount(0); // Clear the table model
                JOptionPane.showMessageDialog(this, "All stats deleted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting all stats: " + e.getMessage());
            } finally {
                closeResources(null, pstmt, conn);
            }
        }
    }

    private void sortStats() {
        int columnIndex = sortComboBox.getSelectedIndex();
        statsTable.getRowSorter().toggleSortOrder(columnIndex);
    }

    private void applyFilter() {
        String playerIdFilter = filterField.getText();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) statsTable.getRowSorter();

        // Create a RowFilter based on the input
        RowFilter<DefaultTableModel, Object> filter;
        if (playerIdFilter.trim().isEmpty()) {
            filter = null; // No filter if the field is empty
        } else {
            filter = RowFilter.regexFilter(playerIdFilter, 0); // Column 0 for Player ID
        }
        sorter.setRowFilter(filter);
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
        SwingUtilities.invokeLater(() -> {
            ViewStatsGUI viewStatsGUI = new ViewStatsGUI();
            viewStatsGUI.setVisible(true);
        });
    }
}
