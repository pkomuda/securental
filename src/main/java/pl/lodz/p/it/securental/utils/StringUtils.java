package pl.lodz.p.it.securental.utils;

import pl.lodz.p.it.securental.exceptions.ApplicationBaseException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException(ApplicationBaseException.KEY_DEFAULT);
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
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i : integers) {
//            stringBuilder.append(i);
//        }
//        return stringBuilder.toString();
        return Arrays.stream(integers)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(""));
    }

    public static String selectCharacters(String text, int[] indices) {
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i : indices) {
//            stringBuilder.append(text.charAt(i));
//        }
//        return stringBuilder.toString();
        return new String(Arrays.stream(indices)
                .map(text::charAt)
                .toArray(), 0, indices.length);
    }

    public static String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String encodeBase64Url(byte[] bytes) {
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    public static byte[] decodeBase64Url(String base64) {
        return Base64.getUrlDecoder().decode(base64);
    }

    public static String randomBase64Url() {
        return encodeBase64Url(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
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

    public static char randomChar(String str) {
        return str.charAt(random(0, str.length() - 1));
    }

    public static boolean isNullOrEmpty(String str) {
        return Objects.isNull(str) || "".equals(str);
    }

    public static BigDecimal stringToBigDecimal(String amount) {
        return new BigDecimal(amount, new MathContext(2, RoundingMode.HALF_UP));
    }

    public static String bigDecimalToString(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toString() : "";
    }
}
