package io.cify.framework.matchers

import io.cify.framework.core.Device
import org.hamcrest.Matcher
import org.openqa.selenium.WebElement

/**
 * Created by FOB Solutions
 *
 * This class contains matchers implementation for TabletIOSApp
 */

class MatchersTabletIOSApp implements IMatchers {

    MatchersTabletIOSApp(Device device) {

    }

    @Override
    Matcher<WebElement> isElementDisplayed() {
        return null
    }
}
