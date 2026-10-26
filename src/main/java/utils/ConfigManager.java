package utils;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;

public class ConfigManager {

    private static final ISettingsFile SETTINGS = new JsonSettingsFile("settings.json");
    private static final String MASKED_VALUE = "********";

    private ConfigManager() {
    }

    public static String getBaseUrlUi() {
        return getString("/baseUrlUi");
    }

    public static String getBaseUrlApi() {
        return getString("/baseUrlApi");
    }

    public static String getToken() {
        return getString("/credentials/token");
    }

    public static String getSessionId() {
        return getString("/sessionId");
    }

    public static String getTextExtension() {
        return getString("/extensions/text");
    }

    public static String getImageExtension() {
        return getString("/extensions/image");
    }

    private static String getString(String jsonPath) {
        String value = SETTINGS.getValue(jsonPath).toString();

        String loggedValue = jsonPath.toLowerCase().contains("token") || jsonPath.toLowerCase().contains("sessionid")
                ? MASKED_VALUE : value;

        AqualityServices.getLogger().info(String.format("Read value from config by path '%s': %s", jsonPath, loggedValue));

        return value;
    }
}