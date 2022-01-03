package pl.edu.pw.gardockt.passwordmanager.security;

public interface PasswordStrengthCalculator {

    int getPasswordStrength(String password);
    int getMinStrength();
    int getMaxStrength();
    String getStrengthLabel(int strength);

}
