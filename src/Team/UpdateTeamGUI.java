package Team;

import DBConnection.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UpdateTeamGUI extends JFrame {

    private int teamId;

    private JTextField teamName, city, owner, venueId;

    public UpdateTeamGUI(int teamId) {

        this.teamId = teamId;

        setTitle("Update Team");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        initializeComponents();


        JButton updateButton = new JButton("Update Team");

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTeamDetails();

            }
        });

        addComponentsToFrame(updateButton);

        populateTeamDetails();


    }

    private void initializeComponents() {
        this.teamName = new JTextField(25);
        this.venueId = new JTextField(25);
        this.city = new JTextField(25);
        this.owner = new JTextField(25);

    }

    private void addComponentsToFrame(JButton updateButton) {


        add(createLabeledComponent("Update teamName", teamName));
        add(createLabeledComponent("Update city", city));
        add(createLabeledComponent("Update owner", owner));
        add(createLabeledComponent("Update home venue id ", venueId));
        add(updateButton);

    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //i think this means the components would be aligned to the left
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Add padding
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }


    private void populateTeamDetails() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = DBConnection.getConnection();
            String sql = "SELECT team_name , city , owner , home_venue_id FROM Team WHERE team_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teamId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // just home_venue_id is going to be sql int data type rest is string i guess
                teamName.setText(rs.getString("team_name"));
                city.setText(rs.getString("city"));
                city.setText(rs.getString("owner"));
                venueId.setText(String.valueOf(rs.getInt("home_venue_id")));
            } else {
                JOptionPane.showMessageDialog(this, "Team not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }


        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching team details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

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

    private void updateTeamDetails() {


        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String updatedTeamName = teamName.getText();
        String updatedCity = city.getText();
        String updatedOwner = owner.getText();
        int updatedVenueId = Integer.parseInt(venueId.getText());

        try {

            conn = DBConnection.getConnection();
            String sql = "UPDATE Team SET team_name = ? , city = ? , owner = ? , home_venue_id = ? WHERE team_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, updatedTeamName);
            pstmt.setString(2, updatedCity);
            pstmt.setString(3, updatedOwner);
            pstmt.setInt(4, updatedVenueId);
            pstmt.setInt(5, teamId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Team details updated successfully!");
                dispose(); // Close the update window after successful update
            } else {
                JOptionPane.showMessageDialog(this, "Error: Team not found or no details were updated.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating team details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
