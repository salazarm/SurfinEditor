package client;

import java.util.regex.Pattern;

public class StringAsciiConversion {
	/**
	 * Transforms a command into a command with
	 * the insert having ASCII code for the server to read.
	 */
	public static String toAscii(String str) {
		if ('\\' == str.charAt(str.length() - 2)
				&& 'n' == str.charAt(str.length() - 1)) {
			return str.substring(0, str.length() - 2) + 10;
		}
		if (str.charAt(0) == 'I')
			return str.substring(0, str.length() - 2) + " "
					+ (int) str.charAt(str.length() - 1);
		return str;
	}

	/**
	 * Converts the document string return by the server into text
	 * from ASCII code.
	 */
	public static String asciiToString(String str) {
		String[] asciiArray = str.split("a");
		String text = "";
		for (String s : asciiArray) {
			if (!Pattern.matches("[\\s]*", s)) {
				int i = Integer.parseInt(s);
				char c = (char) i;
				text += c;
			}
		}
		return text;
	}
}