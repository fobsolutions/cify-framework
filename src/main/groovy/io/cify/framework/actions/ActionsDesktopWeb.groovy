package io.cify.framework.actions

import io.cify.framework.annotations.Title
import io.cify.framework.core.CifyFrameworkException
import io.cify.framework.core.Device
import org.openqa.selenium.WebElement

/**
 * Created by FOB Solutions
 *
 * This class contains actions implementation for DesktopWeb
 */
class ActionsDesktopWeb implements IActions {

    private Device device

    ActionsDesktopWeb(Device device) {
        this.device = device
    }

    @Override
    @Title("Click element")
    public void click(WebElement element) {
        element.click()
    }

    @Override
    @Title("Tap - not supported in DesktopWeb")
    public void tap(WebElement element) {
        throw new CifyFrameworkException("Tap is not supported in DesktopWeb")
    }

    @Override
    @Title("Navigate back")
    public void navigateBack() {
        device.getDriver().navigate().back()
    }

    @Override
    @Title("Fill in element with text")
    public void fillIn(WebElement element, String text) {
        element.sendKeys(text)
    }
}
