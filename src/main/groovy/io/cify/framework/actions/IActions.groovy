package io.cify.framework.actions

import io.cify.framework.core.CifyFrameworkException
import io.cify.framework.core.Device
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * Actions interface
 *
 * Interface with basic actions user can perform
 */
trait IActions {

    /**
     * Wrapper for click
     * @param element WebElement
     */
    void click(WebElement element) {
        try {
            element.click()
        } catch (all) {
            throw new CifyFrameworkException("Failed to click element $element", all)
        }
    }

    /**
     * Wrapper for sendKeys
     * @param element WebElement
     * @param keys text to send to the element
     */
    void sendKeys(WebElement element, String keys) {
        element.sendKeys(keys)
    }

    /**
     * Wrapper for sendKeys
     * @param element WebElement
     * @param key Keys object for special key
     */
    void sendKeys(WebElement element, Keys key) {
        element.sendKeys(key)
    }

    /**
     * Waits for condition
     * @param condition true, false
     * @param timeOut in ms
     * @return True if condition is met
     */
    boolean waitForCondition(Device device, ExpectedCondition condition, long timeOut) {
        try {
            WebDriverWait w = new WebDriverWait(device.getDriver(), timeOut)
            w.until(condition)
        } catch (all) {
            throw new CifyFrameworkException("Waiting for condition failed! " + "CONDITION: " + condition + " cause " + all.message)
        }
    }

    /**
     * Checks if element is displayed
     * @param element WebElement is displayed
     * @return boolean
     */
    static boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed()
        } catch (ignored) {
            return false
        }
    }

    /**
     * Checks if locator is displayed
     *
     * @param device
     * @param locator
     * @return
     */
    static boolean isDisplayed(Device device, By locator) {
        try {
            return device.getDriver().findElement(locator).isDisplayed()
        } catch (ignored) {
            return false
        }
    }

    /**
     * Checks if element is enabled
     * @param element WebElement is enabled
     * @return boolean
     */
    static boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled()
        } catch (ignored) {
            return false
        }
    }

    /**
     * Takes screenshot of the given element
     *
     * @param device
     * @param element
     * @return File
     * */
    static File takeScreenshotOfElement(Device device, WebElement element) {

        // Get entire page screenshot
        File screenshot = ((TakesScreenshot) device.getDriver()).getScreenshotAs(OutputType.FILE)
        BufferedImage fullImg = ImageIO.read(screenshot)

        // Get the location of element on the page
        Point point = element.getLocation()

        // Get width and height of the element
        int elementWidth = element.getSize().getWidth()
        int elementHeight = element.getSize().getHeight()

        // Crop the entire page screenshot to get only element screenshot
        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(),
                elementWidth, elementHeight)
        ImageIO.write(eleScreenshot, "png", screenshot)
        return screenshot
    }
}
