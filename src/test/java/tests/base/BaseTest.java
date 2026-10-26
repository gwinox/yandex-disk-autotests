package tests.base;

import aquality.selenium.browser.AqualityServices;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigManager;
import utils.Screenshot;

public abstract class BaseTest {

    @BeforeMethod
    public void setUp() {
        AqualityServices.getLogger().info(String.format("Setting up browser and navigating to %s", ConfigManager.getBaseUrlUi()));
        AqualityServices.getBrowser().maximize();
        AqualityServices.getBrowser().goTo(ConfigManager.getBaseUrlUi());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult iTestResult) {
        if (iTestResult.getStatus() == ITestResult.FAILURE && AqualityServices.isBrowserStarted()) {
            Screenshot.attachScreenshot();
        }
        if (AqualityServices.isBrowserStarted()) {
            AqualityServices.getLogger().info("Tearing down browser");
            AqualityServices.getBrowser().quit();
        }
    }
}