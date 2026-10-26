package pages;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.forms.Form;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

public class DiskPage extends Form {

    private static final String FILE_LOCATOR_TEMPLATE = "//*[@aria-label='%s']/ancestor::*[@role='row']";

    public DiskPage() {
        super(By.xpath("//*[contains(@data-testid,'smart-global-bar')]"), "Disk");
    }

    private ILabel getFileLabel(String fileName) {
        String locator = String.format(FILE_LOCATOR_TEMPLATE, fileName);
        return getElementFactory().getLabel(By.xpath(locator), fileName);
    }

    @Step("Check that file {fileName} is displayed on Disk")
    public boolean isFileDisplayed(String fileName) {
        return getFileLabel(fileName).state().waitForDisplayed();
    }

    @Step("Check that file {fileName} is not displayed on Disk")
    public boolean isFileNotDisplayed(String fileName) {
        return getFileLabel(fileName).state().waitForNotDisplayed();
    }

    @Step("Move file {fileName} to trash")
    public void moveFileToTrash(String fileName) {
        getFileLabel(fileName).getElement().click();
        new Actions(AqualityServices.getBrowser().getDriver()).sendKeys(Keys.DELETE).perform();
    }

    @Step("Open file {fileName}")
    public void openFile(String fileName) {
        new Actions(AqualityServices.getBrowser().getDriver())
                .doubleClick(getFileLabel(fileName).getElement())
                .perform();
    }
}