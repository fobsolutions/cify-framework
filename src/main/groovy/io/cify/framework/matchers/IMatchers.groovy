package io.cify.framework.matchers

import org.hamcrest.Matcher
import org.openqa.selenium.WebElement

/**
 * Matchers interface (Assertions)
 *
 * Basic matchers that user can perform
 * */
public interface IMatchers {

    Matcher<WebElement> isElementDisplayed()

}
