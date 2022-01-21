package pl.edu.pw.gardockt.passwordmanager.security;

import java.util.Arrays;
import java.util.regex.Pattern;

public class SimplePasswordStrengthCalculator implements PasswordStrengthCalculator {

    private final Pattern lowercaseLetters = Pattern.compile(".*[a-z].*");
    private final Pattern uppercaseLetters = Pattern.compile(".*[A-Z].*");
    private final Pattern digits = Pattern.compile(".*[0-9].*");
    private final Pattern specialCharacters = Pattern.compile(".*[^a-zA-Z0-9].*");

    public int getPasswordStrength(String password) {
        Pattern[] patterns = {lowercaseLetters, uppercaseLetters, digits, specialCharacters};
        int uniqueGroups = (int) Arrays.stream(patterns).filter(p -> p.matcher(password).matches()).count();
        int passwordLength = password.length();
        long uniqueCharacterCount = password.chars().distinct().count();

        int score = (int) Math.round(((double)passwordLength / 3) + ((double)uniqueCharacterCount / 3) + (uniqueGroups * 3));
        return Math.min(Math.max(0, score / 5 - 1), 4);
    }

    public int getMinStrength() {
        return 0;
    }

    public int getMaxStrength() {
        return 4;
    }

    public String getStrengthLabel(int strength) {
        switch(strength) {
            case 0:
                return "bardzo słabe";
            case 1:
                return "słabe";
            case 2:
                return "średnie";
            case 3:
                return "silne";
            case 4:
                return "bardzo silne";
            default:
                return "";
        }
    }

}
