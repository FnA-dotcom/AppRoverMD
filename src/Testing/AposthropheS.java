package Testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AposthropheS {
    static ResultSet rst = null;
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;

    public static void main(String[] args) {
        try {
/*            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
//            String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true";
            String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&autoReconnect=true";
            conn = DriverManager.getConnection(connect_string);*/
            conn = getConnection();
            String Query = "SELECT LTRIM(RTRIM(PayerName)) FROM oe_2.ProfessionalPayers WHERE id=1247 ";
            System.out.println(Query);
            pStmt = conn.prepareStatement(Query);
            rst = pStmt.executeQuery();
            if (rst.next()) {
                System.out.println("First " + rst.getInt(1));
//                System.out.println("Sec " + rst.getString(2));
            }
            rst.close();
            pStmt.close();
/*

            //Query = "insert into Tabish (Test1) VALUE (?)";
            //Query = "UPDATE Tabish SET Test1 = ? WHERE Id = ?";
            pStmt = conn.prepareStatement("UPDATE Tabish SET Test1 = ? WHERE Id = ?");
            pStmt.setString(1, a);
            pStmt.setInt(2, 13);
            pStmt.executeUpdate();
            pStmt.close();
            conn.close();
            System.out.println("CHECK THE TABLE");*/

        } catch (Exception e) {
            conn = null;
            System.out.println("Exception excp conn: " + e.getMessage());
            return;
        }

    }

    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&autoReconnect=true");
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
