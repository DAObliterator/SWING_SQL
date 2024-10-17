package Player;

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
import java.util.Vector;

public class ViewPlayerGUI extends JFrame {
    private JTable playerTable;
    private DefaultTableModel tableModel;
    private JButton deleteSelectedButton, deleteAllButton , updateSelectedButton;
    private JComboBox<String> sortComboBox;
    private JTextField filterNameField, filterPositionField;
    private JSpinner ageMinSpinner, ageMaxSpinner;

    public ViewPlayerGUI() {
        setTitle("View Players");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table model and table setup
        tableModel = new DefaultTableModel(new Object[]{"Player ID", "First Name", "Last Name", "Age", "Position", "Team ID"}, 0);
        playerTable = new JTable(tableModel);

        // Add sorting functionality to table columns
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        playerTable.setRowSorter(sorter);

        loadPlayers();

        JScrollPane scrollPane = new JScrollPane(playerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons for deleting players
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Align buttons to center

        deleteSelectedButton = new JButton("Delete Selected");
        deleteAllButton = new JButton("Delete All");
        updateSelectedButton = new JButton("Update Selected");

        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(deleteAllButton);
        buttonPanel.add(updateSelectedButton);

        // Sorting options
        JPanel sortPanel = new JPanel();
        sortPanel.add(new JLabel("Sort by:"));
        sortComboBox = new JComboBox<>(new String[]{"Player ID", "First Name", "Last Name", "Age", "Position"});
        sortPanel.add(sortComboBox);
        add(sortPanel, BorderLayout.NORTH);

        // Filtering options
        JPanel filterPanel = new JPanel();
        filterPanel.add(new JLabel("Filter by Name:"));
        filterNameField = new JTextField(10);
        filterPanel.add(filterNameField);

        filterPanel.add(new JLabel("Position:"));
        filterPositionField = new JTextField(10);
        filterPanel.add(filterPositionField);

        filterPanel.add(new JLabel("Age Range:"));
        ageMinSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        filterPanel.add(ageMinSpinner);
        filterPanel.add(new JLabel("to"));
        ageMaxSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
        filterPanel.add(ageMaxSpinner);

        JButton filterButton = new JButton("Apply Filter");
        filterPanel.add(filterButton);
        // Combine filterPanel and buttonPanel into one panel
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(filterPanel, BorderLayout.NORTH);
        combinedPanel.add(buttonPanel, BorderLayout.SOUTH);

// Add the combined panel to the SOUTH region
        add(combinedPanel, BorderLayout.SOUTH);

        // Action listener for deleting selected players
        deleteSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedPlayer();
            }
        });

        // Action listener for deleting all players
        deleteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAllPlayers();
            }
        });

        updateSelectedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = playerTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Get the actual row index in the model in case the table is sorted
                    int modelRow = playerTable.convertRowIndexToModel(selectedRow);

                    // Get player ID from the selected row in the table model
                    int playerId = (int) tableModel.getValueAt(modelRow, 0);

                    // Pass playerId to UpdatePlayerGUI
                    UpdatePlayerGUI updatePlayerGUI = new UpdatePlayerGUI(playerId);
                    updatePlayerGUI.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a player to update.");
                }
            }
        });

        // Action listener for sorting
        sortComboBox.addActionListener(e -> sortPlayers());

        // Action listener for applying filters
        filterButton.addActionListener(e -> applyFilters());
    }

    // Load players into the table
    private void loadPlayers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT player_id, first_name, last_name, TIMESTAMPDIFF(YEAR, dob, CURDATE()) AS age, position, team_id FROM Player";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // Load each player into the table model
            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");
                String position = rs.getString("position");
                int teamId = rs.getInt("team_id");
                tableModel.addRow(new Object[]{playerId, firstName, lastName, age, position, teamId});
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

    // Delete selected player from the database
    private void deleteSelectedPlayer() {
        int selectedRow = playerTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get player ID from the selected row
            int playerId = (int) tableModel.getValueAt(selectedRow, 0);

            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM Player WHERE player_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, playerId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Player deleted successfully!");
                tableModel.removeRow(selectedRow); // Remove row from the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting player: " + e.getMessage());
            } finally {
                try {
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a player to delete.");
        }
    }

    // Delete all players from the database
    private void deleteAllPlayers() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all players?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM Player";
                pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "All players deleted successfully!");
                tableModel.setRowCount(0); // Clear the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting players: " + e.getMessage());
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


    // Sort players based on selected option
    private void sortPlayers() {
        int columnIndex = sortComboBox.getSelectedIndex();
        playerTable.getRowSorter().toggleSortOrder(columnIndex);
    }

    // Apply filters to the player table
    private void applyFilters() {
        String nameFilter = filterNameField.getText().toLowerCase();
        String positionFilter = filterPositionField.getText().toLowerCase();
        int ageMin = (Integer) ageMinSpinner.getValue();
        int ageMax = (Integer) ageMaxSpinner.getValue();

        RowFilter<DefaultTableModel, Object> nameFilterRow = RowFilter.regexFilter(nameFilter, 1); // Column 1 for First Name
        RowFilter<DefaultTableModel, Object> positionFilterRow = RowFilter.regexFilter(positionFilter, 4); // Column 4 for Position
        RowFilter<DefaultTableModel, Object> ageFilterRow = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                int age = (Integer) entry.getValue(3); // Column 3 for Age
                return age >= ageMin && age <= ageMax;
            }
        };

        RowFilter<DefaultTableModel, Object> combinedFilter = RowFilter.andFilter(java.util.Arrays.asList(nameFilterRow, positionFilterRow, ageFilterRow));
        ((TableRowSorter<DefaultTableModel>) playerTable.getRowSorter()).setRowFilter(combinedFilter);
    }

    // Main method to launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewPlayerGUI viewPlayerGUI = new ViewPlayerGUI();
            viewPlayerGUI.setVisible(true);
        });
    }
}

