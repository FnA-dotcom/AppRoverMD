package Testing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateCheck {

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
/*            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss a");
            LocalDateTime now = LocalDateTime.now();
            System.out.println(dtf.format(now));
            System.out.println(dtf2.format(now));

            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date NDOS = (Date) simpleDateFormat.parse("2021-12-21T01:15");
            System.out.println("DOS " + NDOS);*/

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.US);
            String strLocalDate = "2021-12-21T01:15";
            LocalDateTime localDate = LocalDateTime.parse(strLocalDate, formatter);

            System.out.println(localDate);
            System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(localDate));
            System.out.println(DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd ").format(localDate));

        } catch (Exception e) {
            System.out.println("Exception excp conn: " + e.getMessage());
            return;
        }

    }
}
