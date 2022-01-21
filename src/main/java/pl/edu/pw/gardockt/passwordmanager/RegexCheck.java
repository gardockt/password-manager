package pl.edu.pw.gardockt.passwordmanager;

import java.util.regex.Pattern;

public class RegexCheck {

	private final static Pattern legalCharactersPattern = Pattern.compile("[\\p{L}\\d_!#$&%()*+,\\-./:<=>?@^|~ ]*");
	private final static Pattern validUsernamePattern = Pattern.compile("[\\w\\-]*");
	private final static Pattern validPasswordPattern = Pattern.compile("[\\w!#$%&()*+,\\-./:<=>?@^|~ ]*");
	private final static Pattern base64Pattern = Pattern.compile("[A-Za-z\\d+/]*=*");

	public static boolean containsOnlyLegalCharacters(String text) {
		return legalCharactersPattern.matcher(text).matches();
	}

	public static boolean isValidUsername(String username) {
		return validUsernamePattern.matcher(username).matches();
	}

	public static boolean isValidPassword(String password) {
		return validPasswordPattern.matcher(password).matches();
	}

	public static boolean isBase64(String text) {
		return base64Pattern.matcher(text).matches();
	}

}
