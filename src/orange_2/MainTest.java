package orange_2;


public class MainTest {

    public static void main(String[] args) throws Exception {

        String datetime = "2020 10      07T155723Z";
        String Date = "";
        String Time = "";
        int num = 12;
        System.out.println(datetime.replaceAll("\\s+", ""));
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "Novermber", "December"};
        if (num >= 1 && num <= 12) {
            System.out.format("The name of month number %d is %s\n", num, months[num - 1]);
        }
//        Date = datetime.substring(0,2) + "-" + datetime.substring(3,5) + "-" + datetime.substring(6,8);
//        System.out.println(Date);
//        System.out.println(datetime.substring(9,15));
//        Time = datetime.substring(9,11) + ":" + datetime.substring(11,13) + ":" + datetime.substring(13,15);
//        System.out.println(Time);

    }

}