package md;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class validationtest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String hello = "é É AAAdddffgg kkk 9999 ' 000 @@@#111%^&$##á";
        System.out.println("Alpha Num only " + getOnlyStrings(hello));
        System.out.println("Alpha only " + getOnlyAlphaNum(hello));
        System.out.println(" Num only " + getOnlyDigits(hello));
        System.out.println("remove ecsape only " + ecscapefilter(hello));
        System.out.println("replace  ecsape only " + encodeQuotes(hello));
    }

    public static String getStringParameter(ServletRequest request, String name, String defaultVal) {
        String val = request.getParameter(name);
        val = StringEscapeUtils.escapeXml(val);
        return (val != null ? val : defaultVal);
    }

    public static String getNumParameter(ServletRequest request, String name, String defaultVal) {
        String val = request.getParameter(name);
        val = getOnlyDigits(val);
        val = StringEscapeUtils.escapeXml(val);

        return (val != null ? val : defaultVal);
    }


    public static String ecscapefilter(String s) {
        s = StringEscapeUtils.escapeXml(s);

        return s;
    }


    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z,0-9:-]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public static String getOnlyAlphaNum(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9.]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public static String encodeQuotes(String s) {
        return s.replace("á", "%C3%A1").replace("é", "%C3%A9").replace("í", "%C3%AD").replace("ó", "%C3%B3").replace("ú", "%C3%BA").replace("ñ", "%C3%B1").
                replace("Á", "%C3%81").replace("É", "%C3%89").replace("Í", "%C3%8D").replace("Ó", "%C3%93").replace("Ú", "%C3%9A").replace("Ñ", "%C3%91");
    }

}
