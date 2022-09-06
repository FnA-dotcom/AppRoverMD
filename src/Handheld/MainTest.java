package Handheld;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
public class MainTest {

    public static void main(String[] args) {
// TODO Auto-generated method stub
        try {
            Connection conn = null;
            String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true";
             String DRIVER = "com.mysql.jdbc.Driver";
//            String DRIVER = "com.mariadb.cj.jdbc.Driver";
            try {
                Class.forName(DRIVER).newInstance();
                conn = DriverManager.getConnection(connect_string);
            } catch (Exception var11) {
                conn = null;
                System.out.println("Exception excp conn: " + var11.getMessage());
                return;
            }
            String Id = "";
            String TraceId = "";
            String msg = "";
            String mrn = "";
            String flag = "";
            String clientid = "";
            Statement hstmt = null;
            Statement hstmt2 = null;
            ResultSet hrset = null;

            String Query = "";

            Query = " SELECT id,msg,mrn,flag,ClientIndex FROM request WHERE ClientIndex in (27,29)";
            System.out.println("Query request Tb: " + Query);
            hstmt = conn.createStatement();
            hrset = hstmt.executeQuery(Query);
            while (hrset.next()) {
                System.out.println(hrset.getString(1));
                System.out.println(hrset.getString(1));
                System.out.println(hrset.getString(2));
                System.out.println(hrset.getString(3));
                System.out.println(hrset.getString(4));
                System.out.println("Client Index " + hrset.getString(5));
            }
            hrset.close();
            hstmt.close();
        } catch (Exception e) {

        }
    }

    private static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance();
//            final Connection connection = DriverManager.getConnection("jdbc:mysql://database-1.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986");
            Connection connection = DriverManager.getConnection("jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true");
            return connection;
        } catch (Exception e) {
            System.out.println("GetConnection: " + e.getMessage());
            return null;
        }
    }


}
