package io.cify.framework.actions

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileDriver
import io.appium.java_client.MultiTouchAction
import io.appium.java_client.TouchAction
import io.appium.java_client.ios.IOSDriver
import io.cify.framework.core.CifyFrameworkException
import io.cify.framework.core.Device
import io.cify.framework.core.DeviceCategory
import io.cify.framework.core.DeviceManager
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebElement

import java.time.Duration

/**
 * Created by FOB Solutions
 *
 * This class contains actions implementation for MobileIosApp
 */

trait ActionsMobileApp implements IActions {

    Device device

    /**
     * Swipe right on an element
     * @param element WebElement to swipe on
     * @param duration duration of the swipe in milliseconds
     */
    void swipeRight(WebElement element, Integer durationInMs) {
        TouchAction action = new TouchAction(device.getDriver() as AppiumDriver)
        List rightTopCoordinates = [element.getLocation().getX() + element.getSize().getWidth(), element.getLocation().getY()]
        List leftTopCoordinates = [element.getLocation().getX(), element.getLocation().getY()]
        action.press(leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1).waitAction(Duration.ofMillis(durationInMs)).moveTo(rightTopCoordinates[0] - 1, rightTopCoordinates[1] + 1).release().perform()
    }

    /**
     * Swipe left on an element
     * @param element WebElement to swipe on
     * @param duration duration of the swipe in milliseconds
     */
    void swipeLeft(WebElement element, Integer durationInMs) {
        TouchAction action = new TouchAction(device.getDriver() as AppiumDriver)
        List rightTopCoordinates = [element.getLocation().getX() + element.getSize().getWidth(), element.getLocation().getY()]
        List leftTopCoordinates = [element.getLocation().getX(), element.getLocation().getY()]
        action.press(rightTopCoordinates[0] - 1, rightTopCoordinates[1] + 1).waitAction(Duration.ofMillis(durationInMs)).moveTo(leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1).release().perform()
    }

    /**
     * Performs scrolling on given element
     * @param device Device instance
     * @param element WebElement to perform scroll on
     * @param direction direction of the scroll: UP, DOWN, RIGHT, LEFT
     */
    static void scrollElement(Device device, WebElement element, String direction) {
        Coordinates c = new Coordinates(element)
        TouchAction action = new TouchAction(device.getDriver() as AppiumDriver)
        int SWIPE_DURATION = 1000
        switch (direction) {
            case "DOWN":
                action.press(c.leftBottomX, c.leftBottomY).waitAction(Duration.ofMillis(SWIPE_DURATION)).moveTo(c.leftTopX, c.leftTopY).release().perform()
                break
            case "UP":
                action.press(c.leftTopX, c.leftTopY).waitAction(Duration.ofMillis(SWIPE_DURATION)).moveTo(c.leftBottomX, c.leftBottomY).release().perform()
                break
            case "RIGHT":
                action.press(c.rightTopX, c.rightTopY).waitAction(Duration.ofMillis(SWIPE_DURATION)).moveTo(c.leftTopX, c.leftTopY).release().perform()
                break
            case "LEFT":
                action.press(c.leftTopX, c.leftTopY).waitAction(Duration.ofMillis(SWIPE_DURATION)).moveTo(c.rightTopX, c.rightTopY).release().perform()
                break
            default: throw new CifyFrameworkException("Scroll direction $direction is not supported")

        }
    }

    /**
     * Tilts the map
     */
    void tilt() {
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
        TouchAction action = new TouchAction(device.getDriver() as AppiumDriver)
        Dimension dimension = device.getDriver().manage().window().getSize()
        int bottomY = dimension.getHeight() - 400
        action.press(dimension.getWidth(), dimension.getHeight()).waitAction(Duration.ofMillis(1000)).moveTo(dimension.getWidth(), bottomY).release().perform()
    }

    /**
     * Swipes from right to left
     */
    void swipeRightToLeft() {
        TouchAction action = new TouchAction(device.getDriver() as AppiumDriver)
        Dimension screenSize = device.getDriver().manage().window().getSize()
        int startX = (screenSize.getWidth() * 0.2)
        int startY = (screenSize.getHeight() * 0.5)
        int endX = (screenSize.getWidth() * 0.8)
        int endY = (screenSize.getHeight() * 0.5)
        action.press(startX, startY).waitAction(Duration.ofMillis(1000)).moveTo(endX, endY).release().perform()
    }

    /**
     * Taps in the middle of the screen (X*Y/2)
     */
    void tapInTheMiddleOfScreen() {
        TouchAction action = new TouchAction(device.getDriver() as AppiumDriver)
        Dimension screenSize = device.getDriver().manage().window().getSize()
        int X = screenSize.getWidth() / 2
        int Y = screenSize.getHeight() / 2
        action.tap(X, Y).perform()
    }

    /**
     * Double taps element
     *
     * @param element - element to double tap
     * */
    void doubleTapElement(WebElement element) {
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
     * Long tap on element
     *
     * @param element - element to long tap
     */
    void longTap(WebElement element) {
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
     */
    void hideKeyboard() {
        try {
            (device.getDriver() as MobileDriver).hideKeyboard()
        } catch (ignored) {
        }
    }

    /**
     * Private class to set and keep coordinates of given WebElement
     * WebElement border coordinates are multiplied by offset multiplier to
     * find a relative location within the element for safe swiping/scrolling
     */
    private static class Coordinates {
        private int rightTopX, rightTopY, leftTopX, leftTopY, leftBottomX, leftBottomY

        private static double OFFSET_MULTIPLIER_10 = 0.1

        Coordinates(WebElement elem) {
            int xOffset = elem.getSize().getWidth() * OFFSET_MULTIPLIER_10
            int yOffset = elem.getSize().getHeight() * OFFSET_MULTIPLIER_10
            rightTopX = elem.getLocation().getX() + elem.getSize().getWidth() - xOffset
            rightTopY = elem.getLocation().getY() + yOffset
            leftTopX = elem.getLocation().getX() + xOffset
            leftTopY = elem.getLocation().getY() + yOffset
            leftBottomX = elem.getLocation().getX() + xOffset
            leftBottomY = elem.getLocation().getY() + elem.getSize().getHeight() - yOffset
        }
    }

}
