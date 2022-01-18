package pl.edu.pw.gardockt.passwordmanager;

public class StringGenerator {

    public static String getLengthError(int minLength, int maxLength) {
        return "wartość powinna mieć długość " + minLength + "-" + maxLength + " znaków";
    }

    public static String getMaxLengthError(int maxLength) {
        return "wartość powinna mieć długość co najwyżej " + maxLength + " znaków";
    }

}
