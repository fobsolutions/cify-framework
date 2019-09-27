package io.cify.framework.core.ui

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class UiUtils {

    /**
     * Gets {@link WebElement} with specific text from the list
     * @param list reference to list {@link WebElement}
     * @param elementBy locator of text element
     * @param text lookup text
     * @return {@link WebElement} with text specified, or null
     */
    static WebElement getListItemWithText(WebElement list, By elementBy, String text) {
        List<WebElement> elements = list.findElements(elementBy)
        for(WebElement element : elements) {
            if (element.text == text) {
                return element
            }
        }
        return null
    }
}
