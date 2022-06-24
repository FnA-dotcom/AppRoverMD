package Testing;

public class TimeCheck {

    public static void main(String[] args) {
        String DOS = "17:13:03";
        String DOS1 = "7:13:03";

        System.out.println("Length 1 --> " + DOS.length());
        System.out.println("Length 2 --> " + DOS1.length());
        //Adding zero in start

        if (DOS1.length() == 7)
            DOS1 = "0" + DOS1;

        System.out.println("DOS 1 --> " + DOS1);
    }
}
