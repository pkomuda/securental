package pl.lodz.p.it.securental.utils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pl.lodz.p.it.securental.exceptions.ApplicationBaseException.KEY_DEFAULT;

public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException(KEY_DEFAULT);
    }

    private static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }

    public static String getTranslatedText(String key, String language) {
        Locale locale;
        if (language.equals("pl")) {
            locale = new Locale(language);
        } else {
            locale = new Locale("en");
        }
        return ResourceBundle.getBundle("messages", locale).getString(key);
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

    public static String base64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String randomBase64() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    public static List<Integer> randomCombination(int full, int min, int max) {
        int amount = random(min, max);
        List<Integer> combination = new ArrayList<>();
        List<Integer> indices = IntStream.range(0, full).boxed().collect(Collectors.toList());
        for (int i = 0; i < amount; i++) {
            int currentIndex = random(0, indices.size() - 1);
            combination.add(indices.get(currentIndex));
            indices.remove(currentIndex);
        }
        Collections.sort(combination);
        return combination;
    }
}
