package io.cify.framework.matchers

import io.cify.framework.annotations.Title
import io.cify.framework.core.models.Device
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement

/**
 * Created by FOB Solutions
 *
 * This class contains matchers implementation for MobileIOSApp
 */

class MatchersMobileIOSApp implements IMatchers {

    private Device device

    MatchersMobileIOSApp(Device device) {
        this.device = device
    }

    @Override
    @Title("Is element displayed")
    public Matcher<WebElement> isElementDisplayed() {
        return new BaseMatcher<WebElement>() {
            @Override
            public boolean matches(Object item) {
                try {
                    return item != null && ((WebElement) item).isDisplayed()
                } catch (WebDriverException e) {
                    return false
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is displayed")
            }
        }
    }
}
