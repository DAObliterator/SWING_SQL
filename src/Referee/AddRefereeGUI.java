package Referee;

import DBConnection.DBConnection; // Update this line
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddRefereeGUI extends JFrame {
    private JTextField refereeNameField, experienceYearsField;

    public AddRefereeGUI() {
        setTitle("Add Referee");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        // Labels and fields
        add(new JLabel("Referee Name:"));
        refereeNameField = new JTextField();
        add(refereeNameField);

        add(new JLabel("Experience (Years):"));
        experienceYearsField = new JTextField();
        add(experienceYearsField);

        JButton submitButton = new JButton("Add Referee");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addReferee();
            }
        });
        add(submitButton);
    }

    // Method to insert a new referee into the database
    private void addReferee() {
        String refereeName = refereeNameField.getText();
        int experienceYears = Integer.parseInt(experienceYearsField.getText());

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO Referee (referee_name, experience_years) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, refereeName);
            pstmt.setInt(2, experienceYears);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Referee added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding referee: " + e.getMessage());
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
