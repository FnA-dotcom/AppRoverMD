package Testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class DateCheck {
    private static Connection conn = null;
    private static PreparedStatement pStmt = null;
    private static String Query = "";

    public static void main(String[] args) {
        try {
/*            String DRIVER = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER).newInstance();
            String connect_string = "jdbc:mysql://127.0.0.1/store?user=root&password=Judean123";
            conn = DriverManager.getConnection(connect_string);


            Query = "insert into DemoTable (AdmissionDate) VALUE (?)";
            pStmt = conn.prepareStatement(Query);
            pStmt.setNull(1, Types.DATE);
            pStmt.executeUpdate();


            System.out.println("CHECK THE TABLE");*/

            String contract_term = "12";
            LocalDate date = LocalDate.parse("2020-05-03");
            // Displaying date
            System.out.println("Date : " + date);
            // Add 2 months to the date
            LocalDate newDate = date.plusMonths(Integer.parseInt(contract_term));
            System.out.println("New Date : " + newDate);


//            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss a");
//            LocalDateTime now = LocalDateTime.now();
//            System.out.println(dtf.format(now));
//            System.out.println(dtf2.format(now));

        } catch (Exception e) {
            conn = null;
            System.out.println("Exception excp conn: " + e.getMessage());
            return;
        }

    }
}
