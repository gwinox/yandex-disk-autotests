package utils;

import aquality.selenium.browser.AqualityServices;
import com.github.javafaker.Faker;

public class TestDataUtils {

    private static final Faker FAKER = new Faker();

    private TestDataUtils() {
    }

    public static String generateRandomWord() {
        String word = FAKER.lorem().word();
        AqualityServices.getLogger().info(String.format("Generated random word: '%s'", word));
        return word;
    }

    public static String generateRandomSentence() {
        String sentence = FAKER.lorem().sentence();
        AqualityServices.getLogger().info(String.format("Generated random sentence: '%s'", sentence));
        return sentence;
    }
}