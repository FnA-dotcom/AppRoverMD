package Testing;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class JSONTableData {
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;
    private static String Query = "";
    private static String Query1 = "";
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public static void main(String[] args) {
        try {
            Statement stmt = null;
            ResultSet rset = null;
            Statement stmt1 = null;
            ResultSet rset1 = null;
            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
            String connect_string = "jdbc:mysql://rovermd-01-master.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=webserver873849&password=Asljdpiwoeurj!!3498&autoReconnect=true";
            conn = DriverManager.getConnection(connect_string);

            System.out.println("CHECK THE TABLE");
            String dbname = "";
            String Id = "";
            HashMap<String, Double> JRespMap = new HashMap<String, Double>();
//            ArrayList<String> amount = new ArrayList<>();
            String _amount = "";
            String ResponseText = "";
            double Amount = 0.0d;
            Query = "SELECT Id, dbname FROM oe.clients WHERE Id IN (1,8,9,10,17,27,28,29)";
//            Query = "SELECT Id, dbname FROM oe.clients WHERE Id = 1";
            stmt = conn.createStatement();
            rset = stmt.executeQuery(Query);
            while (rset.next()) {
//                Id = rset.getString(1);
                dbname = rset.getString(2);


                Query1 = "SELECT Id,JSON_Response FROM " + dbname + ".JSON_Response WHERE RefundFlag = 0 AND VoidFlag = 0 ";
                stmt1 = conn.createStatement();
                rset1 = stmt1.executeQuery(Query1);
                while (rset1.next()) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse("[" + rset1.getString(2) + "]");
                    JSONArray array = (JSONArray) obj;
                    JSONObject obj2 = (JSONObject) array.get(0);

                    ResponseText = (String) obj2.get("resptext");
                    if (ResponseText.equals("Approval")) {
                        _amount = (String) obj2.get("amount");
                        Amount += Double.parseDouble(_amount);
                    }
//                    System.out.println("ResponseText --> " + ResponseText);
                }
                rset1.close();
                stmt1.close();
                JRespMap.put(dbname, Double.valueOf(df2.format(Amount)));
//                System.out.println("dbname --> " + dbname);
            }
            rset.close();
            stmt.close();

//            for (String str : amount)
//                System.out.println(str + " ");
            System.out.println("Iterating Hashmap...");
            for (Map.Entry m : JRespMap.entrySet()) {
                System.out.println(m.getKey() + " --- " + m.getValue());
            }
        } catch (Exception e) {
            conn = null;
            System.out.println("Exception excp conn: " + e.getMessage());
        }

    }
}
