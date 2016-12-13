package io.cify.framework.actions

import io.cify.framework.core.CifyFrameworkException
import io.cify.framework.core.Device
import org.openqa.selenium.WebElement

/**
 * Created by FOB Solutions
 *
 * This class contains actions implementation for TabletAndroidApp
 */

class ActionsTabletAndroidApp implements IActions {

    Device device

    ActionsTabletAndroidApp(Device device) {
        this.device = device
    }

    @Override
    void click(WebElement element) {
        throw new CifyFrameworkException("Not implemented!")
    }

    @Override
    void tap(WebElement element) {
        throw new CifyFrameworkException("Not implemented!")
    }

    @Override
    void navigateBack() {
        throw new CifyFrameworkException("Not implemented!")
    }

    @Override
    void fillIn(WebElement element, String text) {
        throw new CifyFrameworkException("Not implemented!")
    }
}
