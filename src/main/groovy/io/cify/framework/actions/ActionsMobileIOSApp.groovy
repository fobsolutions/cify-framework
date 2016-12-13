package io.cify.framework.actions

import io.appium.java_client.AppiumDriver
import io.appium.java_client.TouchAction
import io.appium.java_client.ios.IOSElement
import io.cify.framework.annotations.Title
import io.cify.framework.core.Device
import org.openqa.selenium.WebElement

/**
 * Created by FOB Solutions
 *
 * This class contains actions implementation for MobileIosApp
 */

class ActionsMobileIOSApp implements IActions {

    private Device device

    ActionsMobileIOSApp(Device device) {
        this.device = device
    }

    @Override
    @Title("Click element")
    public void click(WebElement element) {
        element.click()
    }

    @Override
    @Title("Tap")
    public void tap(WebElement element) {
        new TouchAction((AppiumDriver) device.getDriver()).tap(element).perform()
    }

    @Override
    @Title("Navigate back")
    public void navigateBack() {
        device.getDriver().navigate().back()
    }

    @Override
    @Title("Fill in element with text")
    public void fillIn(WebElement element, String text) {
        if (text.contains("@")) {
            ((IOSElement) element).setValue(text)
        } else {
            element.sendKeys(text)
        }
    }
}
