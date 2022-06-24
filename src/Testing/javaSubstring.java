package Testing;

public class javaSubstring {
    public static void main(String[] args) {
        String DateSympOnset = "04/20//2021";
        //DateSympOnset = DateSympOnset.substring(6, 10) + "-" + DateSympOnset.substring(0, 2) + "-" + DateSympOnset.substring(3, 5);
        DateSympOnset.substring(0, Math.min(10, DateSympOnset.length()));
        System.out.println("DateSympOnset " + DateSympOnset);
    }
}
