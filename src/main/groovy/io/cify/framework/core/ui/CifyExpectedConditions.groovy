package io.cify.framework.core.ui

import org.openqa.selenium.Point
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition

class CifyExpectedConditions {
    static Point lastLocation = new Point(0, 0)

    /**
     * An expectation for checking an element is not moving. Main use case - check for animation end
     *
     * @param element the WebElement
     * @return Instance of ExpectedCondition<Boolean>
     */
    static ExpectedCondition<Boolean> elementToBeStable(final WebElement element) {
        return new ExpectedCondition<Boolean>() {
            @Override
            Boolean apply(WebDriver driver) {
                Point currentLocation = element.location
                try {
                    if (currentLocation.x == lastLocation.x && currentLocation.y == lastLocation.y) {
                        lastLocation = new Point(0, 0)
                        return true
                    }
                    lastLocation.x = currentLocation.x
                    lastLocation.y = currentLocation.y
                    return false
                } catch (StaleElementReferenceException ignored) {
                    return false
                }
            }

            @Override
            String toString() {
                return "element to be stable: " + element
            }
        }
    }
}
