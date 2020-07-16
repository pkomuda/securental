package pl.lodz.p.it.securental.utils;

public class StringUtils {

    public static boolean containsIgnoreCase(String str, String searchStr) {
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }

    public static String intArrayToString(int[] array) {
        StringBuilder str = new StringBuilder();
        for (int i : array) {
            str.append(i);
        }
        return str.toString();
    }

    public static String selectCharacters(String oldText, int[] indices) {
        StringBuilder newText = new StringBuilder();
        for (int i : indices) {
            newText.append(oldText.charAt(i));
        }
        return newText.toString();
    }
}
