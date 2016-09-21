package io.cify.framework.matchers

import io.cify.framework.core.models.Device
import org.hamcrest.Matcher
import org.openqa.selenium.WebElement

/**
 * Created by FOB Solutions
 *
 * This class contains matchers implementation for TabletWeb
 */

class MatchersTabletWeb implements IMatchers {

    MatchersTabletWeb(Device device) {

    }

    @Override
    Matcher<WebElement> isElementDisplayed() {
        return null
    }
}
