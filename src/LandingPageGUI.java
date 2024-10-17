import Coach.CoachOptionsGUI;
import Match.MatchOptionsGUI;
import Referee.RefereeOptionsGUI;
import Sponsor.SponsorOptionsGUI;
import Stats.StatsOptionsGUI;
import Team.TeamOptionsGUI;
import Venue.VenueOptionsGUI;
import Player.PlayerOptionsGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LandingPageGUI extends JFrame {

    public LandingPageGUI() {
        setTitle("NBA Database Management System");
        setSize(600, 600); // Increased size for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on screen
        setLayout(new BorderLayout(20, 20)); // BorderLayout with horizontal and vertical spacing

        // Set frame background color to white
        getContentPane().setBackground(Color.WHITE);

        // Title label with padding and font adjustments
        JLabel titleLabel = new JLabel("NBA Database Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(50, 50, 50)); // Slightly darker gray for contrast
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0)); // Add top and bottom padding

        // Panel for buttons with better layout and spacing
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2, 15, 15)); // 4 rows, 2 columns, 15px spacing between buttons
        buttonPanel.setBackground(Color.WHITE); // Match the panel's background to frame
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Padding around button grid

        // Buttons for each entity
        JButton teamButton = createStyledButton("Team");
        JButton playerButton = createStyledButton("Player");
        JButton refereeButton = createStyledButton("Referee");
        JButton venueButton = createStyledButton("Venue");
        JButton coachButton = createStyledButton("Coach");
        JButton sponsorButton = createStyledButton("Sponsor");
        JButton matchButton = createStyledButton("Match");
        JButton statsButton = createStyledButton("Stats");

        // Add action listeners to open the respective GUIs
        teamButton.addActionListener(e -> {
            TeamOptionsGUI teamOptionsGUI = new TeamOptionsGUI();
            teamOptionsGUI.setVisible(true);
        });

        playerButton.addActionListener(e -> {
            PlayerOptionsGUI playerOptionsGUI = new PlayerOptionsGUI();
            playerOptionsGUI.setVisible(true);
        });

        refereeButton.addActionListener(e -> {
            RefereeOptionsGUI refereeOptionsGUI = new RefereeOptionsGUI();
            refereeOptionsGUI.setVisible(true);
        });

        venueButton.addActionListener(e -> {
            VenueOptionsGUI venueOptionsGUI = new VenueOptionsGUI();
            venueOptionsGUI.setVisible(true);
        });

        coachButton.addActionListener(e -> {
            CoachOptionsGUI coachOptionsGUI = new CoachOptionsGUI();
            coachOptionsGUI.setVisible(true);
        });

        sponsorButton.addActionListener(e -> {
            SponsorOptionsGUI sponsorOptionsGUI = new SponsorOptionsGUI();
            sponsorOptionsGUI.setVisible(true);
        });

        matchButton.addActionListener(e -> {
            MatchOptionsGUI matchOptionsGUI = new MatchOptionsGUI();
            matchOptionsGUI.setVisible(true);
        });

        statsButton.addActionListener(e -> {
            StatsOptionsGUI statsOptionsGUI = new StatsOptionsGUI();
            statsOptionsGUI.setVisible(true);
        });

        // Add buttons to the panel
        buttonPanel.add(teamButton);
        buttonPanel.add(playerButton);
        buttonPanel.add(refereeButton);
        buttonPanel.add(venueButton);
        buttonPanel.add(coachButton);
        buttonPanel.add(sponsorButton);
        buttonPanel.add(matchButton);
        buttonPanel.add(statsButton);

        // Footer panel for spacing
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setPreferredSize(new Dimension(0, 30)); // Add bottom spacing

        // Add components to the frame
        add(titleLabel, BorderLayout.NORTH); // Add title label at the top
        add(buttonPanel, BorderLayout.CENTER); // Add buttons in the center
        add(footerPanel, BorderLayout.SOUTH); // Add some spacing at the bottom
    }

    // Helper method to create styled buttons with hover effect and padding
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setFocusPainted(false); // Remove focus border
        button.setBackground(new Color(240, 240, 240)); // Light gray background
        button.setForeground(Color.DARK_GRAY); // Dark gray text for readability
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),  // Light border around button
                BorderFactory.createEmptyBorder(10, 30, 10, 30) // Padding inside button
        ));

        // Add hover effect to change background color
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 220, 220)); // Darker gray on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240)); // Revert to original background
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LandingPageGUI landingPage = new LandingPageGUI();
            landingPage.setVisible(true);
        });
    }
}
