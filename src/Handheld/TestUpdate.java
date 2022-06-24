package Handheld;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;

public class TestUpdate {
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rset = null;
    private static PreparedStatement pStmt = null;
    private static String Query = "";

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse("[{\"token\":\"9531107331203481\",\"expiry\":\"0124\",\"name\":\"TETSUYA KATSUNISHI\",\"batchid\":\"106\",\"retref\":\"547187051345\",\"avsresp\":\"\",\"respproc\":\"RPCT\",\"amount\":\"250.00\",\"resptext\":\"Approval\",\"authcode\":\"004616\",\"respcode\":\"000\",\"merchid\":\"496406685883\",\"cvvresp\":\"\",\"respstat\":\"A\",\"emvTagData\":\"{\\\"TVR\\\":\\\"0000008000\\\",\\\"PIN\\\":\\\"None\\\",\\\"Signature\\\":\\\"true\\\",\\\"Mode\\\":\\\"Issuer\\\",\\\"Network Label\\\":\\\"MASTERCARD\\\",\\\"TSI\\\":\\\"E800\\\",\\\"ARC\\\":\\\"00\\\",\\\"AID\\\":\\\"A0000000041010\\\",\\\"IAD\\\":\\\"030103A000000000\\\",\\\"Entry method\\\":\\\"Chip Read\\\",\\\"Application Label\\\":\\\"MasterCard\\\"}\",\"orderid\":\"C032UQ02350381-20210216141545\",\"entrymode\":\"EMV Contact\",\"bintype\":\"\"}]");
            JSONArray array = (JSONArray) obj;
            JSONObject obj2 = (JSONObject) array.get(0);
            String token = (String) obj2.get("token");
            String expiry = (String) obj2.get("expiry");
            String name = (String) obj2.get("name");
            String batchid = (String) obj2.get("batchid");
            String retref = (String) obj2.get("retref");
            String avsresp = (String) obj2.get("avsresp");
            String respproc = (String) obj2.get("respproc");
            String amount = (String) obj2.get("amount");
            String resptext = (String) obj2.get("resptext");
            String authcode = (String) obj2.get("authcode");
            String respcode = (String) obj2.get("respcode");
            String merchid = (String) obj2.get("merchid");
            String cvvresp = (String) obj2.get("cvvresp");
            String respstat = (String) obj2.get("respstat");
            String emvTagData = (String) obj2.get("emvTagData");
            String orderid = (String) obj2.get("orderid");
            String entrymode = (String) obj2.get("entrymode");
            String bintype = (String) obj2.get("bintype");

            System.out.println("token " + token + "<br> ");
            System.out.println("expiry " + expiry + "<br> ");
            System.out.println("name " + name + "<br> ");
            System.out.println("batchid " + batchid + "<br> ");
            System.out.println("retref " + retref + "<br> ");
            System.out.println("avsresp " + avsresp + "<br> ");
            System.out.println("respproc " + respproc + "<br> ");
            System.out.println("amount " + amount + "<br> ");
            System.out.println("resptext " + resptext + "<br> ");
            System.out.println("authcode " + authcode + "<br> ");
            System.out.println("respcode " + respcode + "<br> ");
            System.out.println("merchid " + merchid + "<br> ");
            System.out.println("cvvresp " + cvvresp + "<br> ");
            System.out.println("respstat " + respstat + "<br> ");
            System.out.println("emvTagData " + emvTagData + "<br> ");
            System.out.println("orderid " + orderid + "<br> ");
            System.out.println("entrymode " + entrymode + "<br> ");
            System.out.println("bintype " + bintype + "<br> ");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void mainold(String[] args) throws SQLException {
        try {
            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
            String connect_string = "jdbc:mysql://127.0.0.1/store?user=root&password=Judean123";
            conn = DriverManager.getConnection(connect_string);
        } catch (Exception e) {
            conn = null;
            System.out.println("Exception excp conn: " + e.getMessage());
            return;
        }

        String test = "test";
        String test2 = "test2's %^&*%@#$%)(*&& / '\'";
        try {
            pStmt = conn.prepareStatement("INSERT INTO Tabish(Test1,Test2,Test3,Test4) \n" +
                    "VALUES (?,?,?,?) \n" +
                    "ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id), Test1 = VALUES(Test1), Test2 = VALUES(Test2);");

            pStmt.setString(1, "Something is great an is's ");
            pStmt.setString(2, "Tabis's ");
            pStmt.setString(3, test);
            pStmt.setString(4, test2);
            int euReturnValue = pStmt.executeUpdate();
            System.out.printf("executeUpdate returned %d%n", euReturnValue);

            stmt = conn.createStatement();
            rset = stmt.executeQuery("SELECT LAST_INSERT_ID() AS n");
            rset.next();
            int affectedId = rset.getInt(1);
            if (euReturnValue == 1) {
                System.out.printf("    => A new row was inserted: id=%d%n", affectedId);
                System.out.println("Inserted!!");
            } else {
                System.out.printf("    => An existing row was updated: id=%d%n", affectedId);
                System.out.println("UPDATED!!");
            }


        } catch (Exception e) {
            System.out.println("EXCEPTIONS --> " + e.getMessage());
        } finally {
            pStmt.close();
            rset.close();
            stmt.close();
            conn.close();
        }
    }
}
