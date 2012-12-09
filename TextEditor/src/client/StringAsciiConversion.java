package client;

import java.util.regex.Pattern;

public class StringAsciiConversion {
	/**
	 * Converts each individual character in an String in to ASCII Code.
	 */
	public static String toAscii(String str){
		if(str.charAt(0)=='I')
			return str.substring(0,str.length()-2)+" "+(int)str.charAt(str.length()-1);
		return str;
	}
	
	/**
	 * Converts each individual ASCII Code into String.
	 */
	public static String asciiToString(String str){
		System.out.println("asciiToString: "+str);
		String[] asciiArray = str.split("a");
		String text = "";
		for (String s: asciiArray){
			if(!Pattern.matches("[\\s]*", s)){
				int i = Integer.parseInt(s);
				char c = (char) i;
				text += c;
			}
		}
		return text;
	}
}