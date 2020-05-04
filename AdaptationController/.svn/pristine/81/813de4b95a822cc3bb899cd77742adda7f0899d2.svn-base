package utils;

import java.util.List;

public class StringHandle {
	static public boolean checkStringInArray(String str, String [] strArray) {
		for (int i = 0; i < strArray.length; i++) {

	        if (strArray[i].equals(str)) {
	            return true;
	        }
		}
		return false;
	}
	static public boolean checkStringInList(String str, List <String> strArray) {
		for (int i = 0; i < strArray.size(); i++) {

	        if (strArray.get(i).equals(str)) {
	            return true;
	        }
		}
		return false;
	}
	/*
	 * search TRUE/FALSE in variant String
	 * ex: str = *[variant/true] -> out [] = {variant,true}
	 * 
	 */
	static public String [] separateString(String str) {
		String [] strReturn = new String[2];
		String tmp = str.substring(str.indexOf("[")); 
		tmp =	tmp.substring(1, tmp.length()-1);
		strReturn[0] = tmp.substring(0, tmp.indexOf("/"));
		strReturn[1] = tmp.substring(tmp.indexOf("/")+1);
		return strReturn;
	}
	public static void main(String [] arg) {
		String str = "abc[asd/as]";
		String [] st = separateString(str);
		System.out.println(st[0]);
	}
	

}
