package pl.lodz.p.it.securental.utils;

import org.springframework.core.env.Environment;

import java.util.Objects;

public class StringUtils {

    public static boolean containsIgnoreCase(String str, String searchStr) {
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }

    public static String integerArrayToString(int[] integers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i : integers) {
            stringBuilder.append(i);
        }
        return stringBuilder.toString();
    }

    public static String selectCharacters(String text, int[] indices) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i : indices) {
            stringBuilder.append(text.charAt(i));
        }
        return stringBuilder.toString();
    }

    public static String getString(Environment environment, String property) {
        return Objects.requireNonNull(environment.getProperty(property));
    }

    public static int getInteger(Environment environment, String property) {
        return Integer.parseInt(Objects.requireNonNull(environment.getProperty(property)));
    }
}
