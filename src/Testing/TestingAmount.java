package Testing;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.*;
import java.text.DecimalFormat;

public class TestingAmount {
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;
    private static String Query = "";
    private static String Query1 = "";
    private static String ResponseText;
    private static String _amount;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static Statement stmt = null;
    private static ResultSet rset = null;
    private static Statement stmt1 = null;
    private static ResultSet rset1 = null;

    public static void main(String[] args) throws SQLException {
        try {

            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
            String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true";
            conn = DriverManager.getConnection(connect_string);
//129195.54
            double BoltAmountPaid = 0.0d;
            Query = "SELECT JSON_Response,Id FROM richmond.JSON_Response WHERE RefundFlag = 0 AND VoidFlag = 0";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse("[" + rset.getString(1) + "]");
                JSONArray array = (JSONArray) obj;
                JSONObject obj2 = (JSONObject) array.get(0);
                ResponseText = (String) obj2.get("resptext");
                if (ResponseText.equals("Approval")) {
                    _amount = (String) obj2.get("amount");
                    System.out.println(_amount);
                    BoltAmountPaid += Double.parseDouble(_amount);

                    Query1 = "UPDATE richmond.JSON_Response SET BoltAmountPaid = " + _amount + ", ResponseType = 'SUCCESS' " +
                            " WHERE Id = " + rset.getInt(2);
                    stmt1 = conn.createStatement();
                    stmt1.executeUpdate(Query1);
                    stmt1.close();
                } else {
                    Query1 = "UPDATE richmond.JSON_Response SET BoltAmountPaid = 0, ResponseType = 'ERROR' " +
                            " WHERE Id = " + rset.getInt(2);
                    stmt1 = conn.createStatement();
                    stmt1.executeUpdate(Query1);
                    stmt1.close();
                }
            }
            System.out.println("BoltAmountPaid " + df2.format(BoltAmountPaid));
        } catch (Exception Ex) {
            System.out.println("Exception " + Ex.getMessage());
        } finally {
            rset.close();
            stmt.close();
            conn.close();
        }
    }
}
