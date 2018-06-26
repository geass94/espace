package ge.boxwood.espace.config.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileUtils {
    public static Boolean mobileIsValid(String mobile) {
        String mobileRegex = "^5\\d{8}$";
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        Matcher mobileMatcher = mobilePattern.matcher(mobile);

        if (!mobileMatcher.find())
            return false;
        else
            return true;
    }

    public static String formatMobileWithDashes(String mobile) {
        if (!mobileIsValid(mobile)) {
            throw new RuntimeException("Invalid phone number");
        }

        String separator = "-";

        String part1 = mobile.substring(0, 3);
        String part2 = mobile.substring(3, 6);
        String part3 = mobile.substring(6, 9);

        String withDashes = part1.concat(separator).concat(part2).concat(separator).concat(part3);
        return withDashes;
    }
}
