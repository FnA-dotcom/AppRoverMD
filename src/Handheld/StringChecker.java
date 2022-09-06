package Handheld;

public class StringChecker {
    // Returns true if s is
    // a number else false
    private static boolean isNumber(String s)
    {
        for (int i = 0; i < s.length(); i++)
            if (!Character.isDigit(s.charAt(i)))
                return false;

        return true;
    }

    // Driver code
    static public void main(String[] args)
    {
        // Saving the input in a string
        String str = "P6790";

        // Function returns 1 if all elements
        // are in range '0 - 9'
        if (isNumber(str))
            System.out.println("Integer");

            // Function returns 0 if the
            // input is not an integer
        else
            System.out.println("String");
    }
}
