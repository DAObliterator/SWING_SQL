package Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerOptionsGUI extends JFrame {

    public PlayerOptionsGUI() {
        setTitle("Player Options");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JButton addPlayerButton = new JButton("Add Player");
        JButton viewPlayerButton = new JButton("View Players");

        add(addPlayerButton);
        add(viewPlayerButton);

        // Action listener to open AddPlayerGUI
        addPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddPlayerGUI addPlayerGUI = new AddPlayerGUI();
                addPlayerGUI.setVisible(true);
            }
        });

        // Action listener to open ViewPlayerGUI
        viewPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewPlayerGUI viewPlayerGUI = new ViewPlayerGUI();
                viewPlayerGUI.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        PlayerOptionsGUI playerOptionsGUI = new PlayerOptionsGUI();
        playerOptionsGUI.setVisible(true);
    }
}
