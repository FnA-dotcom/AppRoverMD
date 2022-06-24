package Testing;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NPIValidator {

    private static boolean isValid(final String npi) {
        //Picked last digit for Matching check digit at the end
        final char lastDigit = npi.charAt(9);
        //contains doubled digits and unaffected digits
        final List<Integer> alternateDigits = alternativeDigitsDoubled(npi.substring(0, 9));
        int sum = 0;
        //Adding all numerals
        for (final Integer num : alternateDigits) sum += sumOfDigits(num);
        //Add constant 24 as mentioned in algo
        final int total = sum + 24;
        //Picked unitPlaceDigit of total
        final int unitPlaceDigit = total % 10;
        //Subtract from next higher number ending in zero
        final int checkDigit = (unitPlaceDigit != 0) ? (10 - unitPlaceDigit) : unitPlaceDigit;
        return Character.getNumericValue(lastDigit) == checkDigit;
    }

    private static List<Integer> alternativeDigitsDoubled(final String str) {
        final List<Integer> numerals = new ArrayList<>();
        for (int i = 0; i < str.length(); ++i)
            //doubled every alternate digit
            if (i % 2 == 0) numerals.add(2 * Character.getNumericValue(str.charAt(i)));
                //added unaffected digits
            else numerals.add(Character.getNumericValue(str.charAt(i)));
        return numerals;
    }

    private static int sumOfDigits(int num) {
        int sum = 0;
        //Breaking number into single Digits and Adding them
        while (num > 0) {
            sum += num % 10;
            num /= 10;
        }
        return sum;
    }

    public static void main(final String[] args) {

        //should contain any digit and length must be 10
        final String NPI_REGEX = "[0-9]{10}";

        Instant start = Instant.now();

        //NPI example
        String npi = "1124623889";//12254176601;1234567893

        if (npi.matches(NPI_REGEX)) {
            if (isValid(npi))
                System.out.println("NPI is VALID");
            else
                System.out.println("NPI is INVALID");
        } else {
            System.out.println("NPI is INVALID\nmentioned below might be the causes\n(*)NPI should contain Numeric Characters \n" +
                    "(*)NPI length should be 10 digit");
        }
        Instant end = Instant.now();
        System.out.println(Duration.between(start, end)); // prints PT1M3.553S

    }
}