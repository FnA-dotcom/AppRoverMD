package md;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AposthropheS {
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;
    private static String Query = "";

    public static void main(String[] args) {
        try {
            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
            String connect_string = "jdbc:mysql://127.0.0.1/store?user=root&password=Judean123";
            conn = DriverManager.getConnection(connect_string);

            String a = "Test's";

            //Query = "insert into Tabish (Test1) VALUE (?)";
            //Query = "UPDATE Tabish SET Test1 = ? WHERE Id = ?";
            pStmt = conn.prepareStatement("UPDATE Tabish SET Test1 = ? WHERE Id = ?");
            pStmt.setString(1, a);
            pStmt.setInt(2, 10);
            pStmt.executeUpdate();
            pStmt.close();
            conn.close();

            System.out.println("CHECK THE TABLE");


        } catch (Exception e) {
            conn = null;
            System.out.println("Exception excp conn: " + e.getMessage());
            return;
        }

    }
}
