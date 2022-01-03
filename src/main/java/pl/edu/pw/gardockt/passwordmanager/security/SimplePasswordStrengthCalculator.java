package pl.edu.pw.gardockt.passwordmanager.security;

public class SimplePasswordStrengthCalculator implements PasswordStrengthCalculator {

    public int getPasswordStrength(String password) {
        int uniqueGroups = 0;
        int passwordLength = password.length();
        long uniqueCharacterCount = password.chars().distinct().count();

        if(password.matches(".*[a-z].*")) { // lowercase letters
            uniqueGroups++;
        }
        if(password.matches(".*[A-Z].*")) { // uppercase letters
            uniqueGroups++;
        }
        if(password.matches(".*[0-9].*")) { // digits
            uniqueGroups++;
        }
        if(password.matches(".*[^a-zA-Z0-9].*")) { // special characters
            uniqueGroups++;
        }

        int score = (int) Math.round(((double)passwordLength / 3) + ((double)uniqueCharacterCount / 3) + (uniqueGroups * 3));

        if (score < 10) {
            return 0;
        } else if (score < 15) {
            return 1;
        } else if (score < 20) {
            return 2;
        } else if (score < 25) {
            return 3;
        } else {
            return 4;
        }
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
