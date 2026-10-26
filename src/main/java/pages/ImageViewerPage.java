package pages;

import aquality.selenium.elements.interfaces.IButton;
import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.forms.Form;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ImageViewerPage extends Form {

    private final ILabel imageLabel = getElementFactory().getLabel(By.xpath("//*[contains(@class, 'scalable-preview__image')]"),
            "Image Preview"
    );
    private final IButton closeButton = getElementFactory().getButton(By.xpath("//*[contains(@class, 'slider__button_close')]"),
            "Close"
    );

    public ImageViewerPage() {
        super(By.xpath("//*[@role='dialog' and contains(@class, 'slider')]"), "Image Viewer");
    }

    @Step("Check that image preview is displayed")
    public boolean isImageDisplayed() {
        return imageLabel.state().waitForDisplayed();
    }

    @Step("Close image viewer")
    public void clickCloseButton() {
        closeButton.click();
    }
}