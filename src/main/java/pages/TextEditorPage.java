package pages;

import aquality.selenium.elements.interfaces.ILabel;
import aquality.selenium.forms.Form;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;

public class TextEditorPage extends Form {

    private static final String TEXT_CONTENT_CSS = ".mg1";

    private final ILabel shadowHost = getElementFactory().getLabel(By.xpath("//*[@class='__page-1']"), "Shadow Host");

    public TextEditorPage() {
        super(By.xpath("//*[contains(@class, 'pageWithoutSizeWrapper')]"), "Text editor");
    }

    @Step("Get text from editor")
    public String getFileText() {
        shadowHost.state().waitForDisplayed();
        SearchContext shadowRoot = shadowHost.getElement().getShadowRoot();
        return shadowRoot.findElement(By.cssSelector(TEXT_CONTENT_CSS)).getText();
    }
}