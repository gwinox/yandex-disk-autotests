package utils;

import aquality.selenium.browser.AqualityServices;
import io.qameta.allure.Attachment;

public class Screenshot {

    private Screenshot() {
    }

    @Attachment(value = "Screenshot", type = "image/png", fileExtension = ".png")
    public static byte[] attachScreenshot() {
        return AqualityServices.getBrowser().getScreenshot();
    }
}
