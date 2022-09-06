package Testing;

import java.sql.*;

public class AposthropheS {
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;
    private static String Query = "";
    private static Statement stmt = null;
    private static ResultSet rset = null;

    public static void main(String[] args) {
        try {
//            String DRIVER = "com.mysql.jdbc.Driver";
            String DRIVER = "com.mysql.cj.jdbc.Drive";
            Class.forName(DRIVER).newInstance();
//            String connect_string = "jdbc:mysql://127.0.0.1/store?user=root&password=Judean123";
            String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986";
            conn = DriverManager.getConnection(connect_string);

            String a = "Test's/test";
            Query = "Select * from oe_2.PatientReg where MRN = 310599";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                a = rset.getString(2);
            }
            rset.close();
            stmt.close();
            //Query = "insert into Tabish (Test1) VALUE (?)";
            //Query = "UPDATE Tabish SET Test1 = ? WHERE Id = ?";
//            pStmt = conn.prepareStatement("UPDATE Tabish SET Test1 = ? WHERE Id = ?");
//            pStmt.setString(1, a);
//            pStmt.setInt(2, 13);
//            pStmt.executeUpdate();
//            pStmt.close();
//            conn.close();

            System.out.println("CHECK THE TABLE");


        } catch (Exception e) {
            conn = null;
            System.out.println("Exception excp conn: " + e.getMessage());
            return;
        }

    }
}
