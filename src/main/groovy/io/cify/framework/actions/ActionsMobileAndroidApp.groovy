package io.cify.framework.actions

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileDriver
import io.appium.java_client.MultiTouchAction
import io.appium.java_client.TouchAction
import io.appium.java_client.android.AndroidDriver
import io.cify.framework.core.CifyFrameworkException
import io.cify.framework.core.Device
import io.cify.framework.core.DeviceCategory
import io.cify.framework.core.DeviceManager
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebElement

/**
 * Created by FOB Solutions
 *
 * This class contains actions implementation for MobileAndroidApp
 */

trait ActionsMobileAndroidApp implements IActions {

    /**
     * Swipe right on an element
     * @param element WebElement to swipe on
     * @param duration duration of the swipe in ms
     */
    void swipeRight(WebElement element, Integer durationInSeconds) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        List rightTopCoordinates = [element.getLocation().getX() + element.getSize().getWidth(), element.getLocation().getY()]
        List leftTopCoordinates = [element.getLocation().getX(), element.getLocation().getY()]
        (device.getDriver() as AndroidDriver).swipe(leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1, rightTopCoordinates[0] - 1, rightTopCoordinates[1] + 1, durationInSeconds)
    }

    /**
     * Swipe left on an element
     * @param element WebElement to swipe on
     * @param duration duration of the swipe in ms
     */
    void swipeLeft(WebElement element, Integer durationInSeconds) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        List rightTopCoordinates = [element.getLocation().getX() + element.getSize().getWidth(), element.getLocation().getY()]
        List leftTopCoordinates = [element.getLocation().getX(), element.getLocation().getY()]
        (device.getDriver() as AndroidDriver).swipe(rightTopCoordinates[0] - 1, rightTopCoordinates[1] + 1, leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1, durationInSeconds)
    }

    /**
     * Tilts the map
     * @param device Device
     */
    void tilt() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        TouchAction action1 = new TouchAction(device.getDriver() as MobileDriver)
        TouchAction action2 = new TouchAction(device.getDriver() as MobileDriver)
        MultiTouchAction action = new MultiTouchAction(device.getDriver() as MobileDriver)
        Dimension screenSize = device.getDriver().manage().window().getSize()

        action1.press((screenSize.getWidth() * 0.4).toInteger(), (screenSize.getHeight() * 0.5).toInteger()).moveTo(0, (-screenSize.getHeight() * 0.2).toInteger()).release()
        action2.press((screenSize.getWidth() * 0.6).toInteger(), (screenSize.getHeight() * 0.5).toInteger()).moveTo(0, (-screenSize.getHeight() * 0.2).toInteger()).release()

        action.add(action1).add(action2).perform()
    }

    /**
     * Scrolls down
     * */
    void scrollDown() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        Dimension dimension = device.getDriver().manage().window().getSize()
        int bottomY = dimension.getHeight() - 400
        (device.getDriver() as AppiumDriver).swipe(dimension.getWidth(), dimension.getHeight(), dimension.getWidth(), bottomY, 1000);
    }

    /**
     * Swipes from right to left
     * @param device
     */
    void swipeRightToLeft() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        Dimension screenSize = device.getDriver().manage().window().getSize()
        int startX = (screenSize.getWidth() * 0.2)
        int startY = (screenSize.getHeight() * 0.5)
        int endX = (screenSize.getWidth() * 0.8)
        int endY = (screenSize.getHeight() * 0.5)
        (device.getDriver() as MobileDriver).swipe(startX, startY, endX, endY, 1000)
    }
    /**
     * Taps in the middle of the screen (X*Y/2)
     * @param device
     */
    void tapInTheMiddleOfScreen() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        Dimension screenSize = device.getDriver().manage().window().getSize()
        int X = screenSize.getWidth() / 2
        int Y = screenSize.getHeight() / 2
        (device.getDriver() as MobileDriver).tap(1, X, Y, 100)
    }

    /**
     * Double taps element
     *
     * @param device - device to perform the tap
     * @param element - element to double tap
     * */
    void doubleTapElement(WebElement element) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        MobileDriver driver = device.getDriver() as MobileDriver
        MultiTouchAction multiTouch = new MultiTouchAction(driver)
        TouchAction action = new TouchAction(driver).press(element).release().press(element).release()
        try {
            multiTouch.add(action).perform()
        } catch (all) {
            throw new CifyFrameworkException("Failed to double tap element cause $all.message")
        }
    }

    /**
     * Zooms in on given element
     * @param device
     * @param element
     */
    void zoom(WebElement element) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        (device.getDriver() as AppiumDriver).zoom(element)
    }

    /**
     * Zooms out on given element
     * @param device
     * @param element
     */
    void pinch(WebElement element) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        (device.getDriver() as AppiumDriver).pinch(element)
    }

    /**
     * Long tap on element
     *
     * @param device - device to perform the long tap
     * @param element - element to long tap
     */
    static void longTap(WebElement element) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        MobileDriver driver = device.getDriver() as MobileDriver
        TouchAction action = new TouchAction(driver).longPress(element)
        try {
            action.perform()
        } catch (all) {
            throw new CifyFrameworkException("Failed to long tap element cause $all.message")
        }
    }

    /**
     * Hides soft keyboard
     * @param device
     */
    static void hideKeyboard() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.ANDROID)
        try {
            (device.getDriver() as MobileDriver).hideKeyboard()
        } catch (ignored) {
        }
    }

}
