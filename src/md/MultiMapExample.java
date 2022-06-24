package md;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.sql.*;

public class MultiMapExample {
    static ResultSet rst = null;
    static Statement stmt = null;
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;

    public static void main(String[] args) {
        Multimap<String, Integer> multimap = ArrayListMultimap.create();
        try {
            String Query1 = "";
            ResultSet resultSet = null;
            Statement statement = null;
            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
            String connect_string = "jdbc:mysql://database-1.cvsodt2nhyzz.us-east-1.rds.amazonaws.com/oe?user=rovermdadmin&password=atyu!ioujy1986&autoReconnect=true";
            conn = DriverManager.getConnection(connect_string);
            String Query = "SELECT indexptr,userid FROM oe.sysusers WHERE usertype = 10";
            stmt = conn.createStatement();
            rst = stmt.executeQuery(Query);
            while (rst.next()) {
                Query1 = "SELECT FacilityIdx FROM oe.AdvocateSMSNumber WHERE AdvocateIdx = " + rst.getInt(1);
                statement = conn.createStatement();
                resultSet = statement.executeQuery(Query1);
                while (resultSet.next()) {
                    multimap.put(rst.getString(2), resultSet.getInt(1));
                }
                resultSet.close();
                statement.close();
            }
            rst.close();
            stmt.close();

            System.out.println(multimap);
//            System.out.println(multimap.get("gladys"));
/*            ArrayList<Collection<Integer>> listFac = new ArrayList<>();
            listFac.add(multimap.get("gladys").contains(9));
            System.out.println(listFac);
            boolean ans = listFac.contains(9);
            System.out.println(ans);*/
            System.out.println(multimap.get("gladys").contains(19));
        } catch (Exception e) {
            conn = null;
            System.out.println("Exception excp conn: " + e.getMessage());
            return;
        }
    }
}
