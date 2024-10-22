package TeamLeaderboard;

import DBConnection.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeamLeaderBoardGUI extends JFrame {


    public TeamLeaderBoardGUI() {

        setTitle("View TeamLeaderBoard");
        setSize(500 , 400);
        DefaultTableModel teamLeaderboardModel = new DefaultTableModel( new Object[] { "Team_Name" , "Arena" , "Key_Player" , "Points"} , 0 );
        JTable teamLeaderboardTable = new JTable(teamLeaderboardModel);
        add(teamLeaderboardTable);

        JScrollPane scrollLeaderboardTable = new JScrollPane(teamLeaderboardTable);
        add(scrollLeaderboardTable , BorderLayout.CENTER);


    }


    private void loadTeamLeaderboard() {

        //go to matches relation find using group aggregate method teams and their points using the necessaru condition

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;

        try {
            conn = DBConnection.getConnection();
            //String sql1 = "SELECT team_id FROM PLAYER WHERE player_id IN (SELECT SUM(points) , player_id FROM Stats GROUP BY player_id)";
            String sql = "";
            String sql1 = "SELECT p.player_name ";
            pstmt = conn.prepareStatement(sql);

            rs = pstmt.executeQuery();

            while ( rs.next()) {

            }


        } catch ( Exception e ) {

        } finally {

        }


    }

}
