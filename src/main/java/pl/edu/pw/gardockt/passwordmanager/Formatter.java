package pl.edu.pw.gardockt.passwordmanager;

import java.sql.Timestamp;

public class Formatter {

	public static String formatDate(Timestamp date) {
		if(date != null) {
			return date.toString().replaceFirst("\\.\\d+$", "");
		} else {
			return "";
		}
	}

}
