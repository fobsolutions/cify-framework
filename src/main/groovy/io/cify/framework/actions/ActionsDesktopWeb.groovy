package io.cify.framework.actions

import io.cify.framework.core.Device
import io.cify.framework.core.DeviceCategory
import io.cify.framework.core.DeviceManager
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.internal.Coordinates
import org.openqa.selenium.internal.Locatable
import org.openqa.selenium.support.ui.Select

/**
 * Created by FOB Solutions
 *
 * This class contains actions implementation for DesktopWeb
 */
trait ActionsDesktopWeb implements IActions {

    /**
     * Opens URL
     */
    void open(String url) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        device.getDriver().get(url)
    }

    /**
     * Scrolls element into view
     * @param element
     */
    void scrollIntoView(WebElement element) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        ((JavascriptExecutor) device.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element)
    }

    /**
     * Scrolls on web page based on pixel values
     *
     * @param x pixel value
     * @param y pixel value
     */
    void scrollTo(int x, int y) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        ((JavascriptExecutor) device.getDriver()).executeScript("scrollTo(" + x + "," + y + ")")
    }

    /**
     * Scrolls to element, gets coordinates based on element
     *
     * @param element element to scroll to
     */
    void scrollToElement(WebElement element) {
        Coordinates c = ((Locatable) element).getCoordinates()
        c.onPage()
        c.inViewPort()
    }

    /**
     * Scrolls vertically inside element, expects child element to be lower that parent element
     * Calculates difference between child and parent element and scrolls in the parent element that much
     *
     * @param parent element inside what to scroll
     * @param child element to scroll to
     */
    void scrollVerticallyInElement(WebElement parent, WebElement child) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        int y = child.getLocation().getY() - parent.getLocation().getY()
        ((JavascriptExecutor) device.getDriver()).executeScript("arguments[0].scrollTop = arguments[1];", parent, y)
    }

    /**
     * Gets bounds of a web element
     *
     * @param we WebElement to get bounds of
     * @return string array list [from left, from top, width, height]
     */
    List<String> getBoundedRectangleOfElement(WebElement we) {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        JavascriptExecutor je = (JavascriptExecutor) device.getDriver()
        List<String> bounds = (ArrayList<String>) je.executeScript(
                "var rect = arguments[0].getBoundingClientRect();" +
                        "return [ '' + parseInt(rect.left), '' + parseInt(rect.top), '' + parseInt(rect.width), '' + parseInt(rect.height) ]", we)
        bounds
    }

    /**
     * Gets current URL of the web browser
     */
    String getCurrentUrl() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        device.getDriver().getCurrentUrl()
    }

    /**
     * Gets title of the website
     */
    String getTitle() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        device.getDriver().getTitle()
    }

    /**
     * Navigates back
     */
    void back() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        device.getDriver().navigate().back()
    }

    /**
     * Navigates forward
     */
    void forward() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        device.getDriver().navigate().forward()
    }

    /**
     * Refreshes the page
     */
    void refresh() {
        Device device = DeviceManager.getInstance().getActiveDevice(DeviceCategory.BROWSER)
        device.getDriver().navigate().refresh()
    }

    /**
     * Selects from dropdown by visible text
     * @param text
     */
    void selectByVisibleText(WebElement element, String text) {
        Select select = new Select(element)
        select.deselectAll()
        select.selectByVisibleText(text)
    }

    /**
     * Selects from dropdown by value
     * @param value
     */
    void selectByValue(WebElement element, String value) {
        Select select = new Select(element)
        select.deselectAll()
        select.selectByValue(value)
    }

    /**
     * Deselects all from select box
     */
    void deselectAllFromSelect(WebElement element) {
        Select select = new Select(element)
        select.deselectAll()
    }

    /**
     * Gets all selected elements
     * @param element
     * @return list of select box elements
     */
    List<WebElement> getSelectedElements(WebElement element) {
        Select select = new Select(element)
        select.getAllSelectedOptions()
    }

    /**
     * Checks if radio button is selected
     */
    boolean isSelected(WebElement element) {
        element.isSelected()
    }
}
