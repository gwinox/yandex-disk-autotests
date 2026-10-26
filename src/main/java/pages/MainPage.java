package pages;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.forms.Form;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class MainPage extends Form {

    private final IButton goToYandexDiskButton = getElementFactory().getButton(By.xpath("//*[@data-test='hero']//*[@data-test='btn']//a"),
            "Go to Yandex Disk");

    public MainPage() {
        super(By.xpath("//*[@data-test='panel']"), "Main Page");
    }

    @Step("Click 'Go to Yandex Disk' button")
    public void clickGoToYandexDiskButton() {
        goToYandexDiskButton.click();
    }
}