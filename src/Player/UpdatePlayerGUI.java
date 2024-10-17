package Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import DBConnection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdatePlayerGUI extends JFrame {

    private int playerId;

    private JTextField firstName, lastName ;
    private  JComboBox <String> position;
    private final String[] positions = {"Point Guard", "Shooting Guard", "Small Forward", "Power Forward", "Center"};

    public UpdatePlayerGUI(int playerId) {

        this.playerId = playerId;
        // Set up the frame
        setTitle("Update Player");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


        // Initialize components
        initializeComponents();



        // Create and add action button
        JButton updateButton = new JButton("Update Player");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePlayerDetails();
            }
        });

        // Add components to the frame
        addComponentsToFrame(updateButton);

        populatePlayerDetails();

    }


    private void initializeComponents () {

        firstName = new JTextField(10);
        lastName = new JTextField(10);
        position = new JComboBox<>(positions);

    }

    private void addComponentsToFrame(JButton updateButton) {
        add(createLabeledComponent("First Name:", firstName));
        add(createLabeledComponent("Last Name:", lastName));
        add(createLabeledComponent("Position:", position)); // Use positionComboBox
        add(Box.createRigidArea(new Dimension(0, 10))); // Add space before the button
        add(updateButton);
    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Add padding
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    private void populatePlayerDetails() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT first_name, last_name, position FROM Player WHERE player_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playerId); //first ? in the query
            rs = pstmt.executeQuery();

            if (rs.next()) {
                firstName.setText(rs.getString("first_name"));
                lastName.setText(rs.getString("last_name"));
                String positionText = rs.getString("position");

                // Set the selected item in the position combo box
                for (int i = 0; i < positions.length; i++) {
                    if (positions[i].equals(positionText)) {
                        position.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Player not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching player details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void updatePlayerDetails() {
        // Get the updated details from the form fields
        String updatedFirstName = firstName.getText();
        String updatedLastName = lastName.getText();
        String updatedPosition = (String) position.getSelectedItem();

        // Check if fields are not empty
        if (updatedFirstName.isEmpty() || updatedLastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name and Last Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE Player SET first_name = ?, last_name = ?, position = ? WHERE player_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, updatedFirstName);
            pstmt.setString(2, updatedLastName);
            pstmt.setString(3, updatedPosition);
            pstmt.setInt(4, playerId);  // Use the playerId passed to the constructor

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Player details updated successfully!");
                dispose(); // Close the update window after successful update
            } else {
                JOptionPane.showMessageDialog(this, "Error: Player not found or no details were updated.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating player details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

