package md;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class CleanerConn2 {

    public static void main(String[] args) {
        Statement stmt = null;
        Statement stmt1 = null;
        ResultSet rset = null;
        Connection conn = null;
        String Query = "";
        String Query1 = "";

        String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&characterEncoding=utf8";
//        String DRIVER = "com.mysql.jdbc.Driver";
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        try {
            Class.forName(DRIVER).newInstance();
            conn = DriverManager.getConnection(connect_string);
        } catch (Exception var11) {
            conn = null;
            System.out.println("Exception excp conn: " + var11.getMessage());
            DumpException("DB Conn Error ", "exp", var11);
            return;
        }
        //conn = getConnection();
        if (conn == null) {
            System.out.println("UNABLE TO CONNECT TO DB!!");
            return;
        }
        System.out.println("Connected to Database... ");

        Query = "SELECT concat('KILL ',id,';'),Host,DB,TIME ,STATE,INFO,USER,Command  " +
                "FROM INFORMATION_SCHEMA.PROCESSLIST WHERE Command='Sleep' " +
                "AND USER NOT IN ('event_scheduler','rdsadmin','rovermdadmin','Dev01kolkka') ";
        try {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            DumpMessages("StartLine", "************************************************************");
            while (rset.next()) {
                DumpMessages("HOST --> ", rset.getString(2));
                DumpMessages("DB --> ", rset.getString(3));
                DumpMessages("TIME --> ", rset.getString(4));
                DumpMessages("STATE --> ", rset.getString(5));
                DumpMessages("INFO --> ", rset.getString(6));
                DumpMessages("USER --> ", rset.getString(7));
                DumpMessages("Command --> ", rset.getString(8));

                Query1 = "" + rset.getString(1);
                DumpMessages("Query1 --> ", Query1);
                try {
                    stmt1 = conn.createStatement();
                    stmt1.executeUpdate(Query1);
                    stmt1.close();
                } catch (Exception Ex) {
                    System.out.println("Error while Cleaning Conn " + Ex.getMessage());
                }
            }
            DumpMessages("EndLine", "***************************************************************");
            rset.close();
            stmt.close();
        } catch (Exception Ex) {
            System.out.println("Error while Cleaning Conn " + Ex.getMessage());
            DumpException("main", "exp", Ex);
            return;
        }
    }

    private static void DumpException(String method, String message, Exception exception) {
        try {
            FileWriter filewriter = new FileWriter("/sftpdrive/opt/home/applications/logs/CleanerExceptions.log", true);

            filewriter.write(new Date().toString() + "^" + method + "^" + message + "^" + exception.getMessage() + "\r\n");

            PrintWriter printwriter = new PrintWriter(filewriter, true);
            exception.printStackTrace(printwriter);
            filewriter.write("\r\n");
            filewriter.flush();
            filewriter.close();
            printwriter.close();
        } catch (Exception localException) {
        }
    }

    private static void DumpMessages(String method, String message) {
        String s2 = "";
        try {
            FileWriter filewriter = new FileWriter("/sftpdrive/opt/home/applications/logs/CleanerClassMessages.log", true);

            filewriter.write(new Date().toString() + "^ " + method + " ^ " + message + "\r\n");

            PrintWriter printwriter = new PrintWriter(filewriter, true);
            //filewriter.write("\r\n");
            filewriter.flush();
            filewriter.close();
            printwriter.close();
        } catch (Exception localException) {
        }
    }
}
