package Testing;

import java.sql.*;

public class LongTesting {
    private static Statement stmt = null;
    private static ResultSet rset = null;
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;

    public static void main(String[] args) throws SQLException {
        try {
            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
            String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/tabish?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true";
            conn = DriverManager.getConnection(connect_string);

            int Value1 = 10;
            long Value2 = 90L;
            int Value3 = 1;
            String Value4 = "0000007";
            Value2 = Long.parseLong("000000") + Value2;
/*            pStmt = conn.prepareStatement("INSERT INTO tabish.ValuesTesting (Value1, Value2, Value3, Value4) " +
                    "VALUES (?,?,?,?)");
            pStmt.setInt(1, Value1);
            pStmt.setLong(2, Value2);
            pStmt.setInt(3, Value3);
            pStmt.setString(4, Value4);
            pStmt.executeUpdate();
            pStmt.close();
            System.out.println("CHECK THE TABLE");*/

            long Value5 = 0L;
            String Value6 = "";
            String Query = "SELECT MAX(Value4) ,MAX(Value4)+1 FROM tabish.ValuesTesting";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            if (rset.next()) {
                Value5 = rset.getLong(2);
                Value6 = rset.getString(2);
                Value4 = rset.getString(1);
            }
            rset.close();
            stmt.close();

            System.out.println("Value 4 --> " + Value4);
            System.out.println("Value 5 --> " + Value5);
            System.out.println("Value 6 --> " + Value6);
//            long unixTime = System.currentTimeMillis() / 1000L;
//            System.out.println("Value 7 --> " + unixTime);
//            SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
//            jdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
//            String java_date = jdf.format(unixTime);
//            System.out.println("\n"+java_date+"\n");
/*            long i = 0000000123;
            int j = 0000000123;
            long lobject = 77387187L;
            Long lobject1 = new Long(0000000123);
            System.out.println(" Value of i " + i);
            System.out.println(" Value of J " + j);
            System.out.println(" Value of Long " + lobject);
            System.out.println(" Value of Long " + lobject1);
            String str = "0000000123";
            System.setProperty(str, "0000000123");
            Long l = Long.getLong(str);
            System.out.println("The Long value is given as : " + l);*/
        } catch (Exception Ex) {
            System.out.println("Exception " + Ex.getMessage());
        } finally {
            conn.close();
        }
    }
}
