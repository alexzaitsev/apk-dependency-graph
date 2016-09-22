package code;


public class StringUtils {

	public static boolean isNumber(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}
}
