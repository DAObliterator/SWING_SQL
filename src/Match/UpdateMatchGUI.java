package Match;

import DBConnection.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateMatchGUI extends JFrame {
    private JTextField homeTeam, awayTeam, winnerTeam, referee, venue, date;

    private int matchId;



    public UpdateMatchGUI(int matchId) {
        this.matchId = matchId;

        setTitle("Update Match");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        initializeComponents();

        JButton updateButton = new JButton("Update Match");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMatchDetails();
            }
        });

        addComponentsToFrame(updateButton);

        populateMatchDetails();


    }

    private void initializeComponents() {

        homeTeam = new JTextField(25);
        awayTeam = new JTextField(25);
        winnerTeam = new JTextField(25);
        referee = new JTextField(25);
        venue = new JTextField(25);
        date = new JTextField(25);

    }

    private void addComponentsToFrame(JButton updateButton) {

        add(createLabeledComponent("home Team", homeTeam));
        add(createLabeledComponent("away Team", awayTeam));
        add(createLabeledComponent("winner Team", winnerTeam));
        add(createLabeledComponent("referee", referee));
        add(createLabeledComponent("date (YYYY-MM-DD)", date));
        add(createLabeledComponent("venue", venue));
        add(updateButton);
    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Add padding
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }


    private void populateMatchDetails() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            /* you have the matchId using this you need to find the current match Details */
            conn = DBConnection.getConnection();
            String sql = "SELECT home_team_id , away_team_id , match_date , venue_id , winner_team_id FROM `Match` WHERE match_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId); //first ? in the query
            rs = pstmt.executeQuery();


            if (rs.next()) {

                homeTeam.setText(String.valueOf(rs.getInt("home_team_id")));
                awayTeam.setText(String.valueOf(rs.getInt("away_team_id")));
                winnerTeam.setText(String.valueOf(rs.getInt("winner_team_id")));
                venue.setText(String.valueOf(rs.getInt("venue_id")));

                // Retrieve the SQL date as java.sql.Date and convert to LocalDate
                LocalDate localDate = rs.getDate("match_date").toLocalDate();

                // Define the output string format using DateTimeFormatter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Format the date to a string
                String formattedDate = localDate.format(formatter);

                date.setText(formattedDate);

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

    private void updateMatchDetails() {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int updatedHomeTeam = Integer.parseInt(homeTeam.getText());
        int updatedAwayTeam = Integer.parseInt(awayTeam.getText());
        int updatedWinnerTeam = Integer.parseInt(winnerTeam.getText());
        int updatedVenue = Integer.parseInt(venue.getText());
        String updatedDateStr = date.getText();
        java.sql.Date updatedDate = java.sql.Date.valueOf(updatedDateStr);


        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE `MATCH` SET home_team_id = ?, away_team_id = ? , venue_id = ?, winner_team_id = ?, match_date = ? WHERE match_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, updatedHomeTeam);
            pstmt.setInt(2, updatedAwayTeam);
            pstmt.setInt(3, updatedVenue);
            pstmt.setInt(4, updatedWinnerTeam);
            pstmt.setDate(5, updatedDate);
            pstmt.setInt(6 ,matchId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Match details updated successfully!");
                dispose(); // Close the update window after successful update
            } else {
                JOptionPane.showMessageDialog(this, "Error: Match not found or no details were updated.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating match details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
