package io.cify.framework.actions

import io.cify.framework.core.Device
import io.cify.framework.core.DeviceManager
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.internal.Coordinates
import org.openqa.selenium.internal.Locatable
import org.openqa.selenium.support.ui.Select

import static io.cify.framework.core.DeviceCategory.BROWSER

/**
 * Created by FOB Solutions
 *
 * This class contains actions implementation for DesktopWeb
 */
trait ActionsDesktopWeb implements IActions {

    /**
     * Opens given URL
     * @param url
     */
    void open(String url) {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        device.getDriver().get(url)
    }

    /**
     * Scrolls element into view
     * @param element
     */
    void scrollIntoView(WebElement element) {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        ((JavascriptExecutor) device.getDriver()).executeScript("arguments[0].scrollIntoView(true);", element)
    }

    /**
     * Scrolls on web page based on pixel values
     *
     * @param x pixel value
     * @param y pixel value
     */
    void scrollTo(int x, int y) {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        ((JavascriptExecutor) device.getDriver()).executeScript("scrollTo(" + x + "," + y + ")")
    }

    /**
     * Scrolls to element
     *
     * @param element to scroll to
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
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        int y = child.getLocation().getY() - parent.getLocation().getY()
        ((JavascriptExecutor) device.getDriver()).executeScript("arguments[0].scrollTop = arguments[1];", parent, y)
    }

    /**
     * Gets bounds of a web element
     *
     * @param element WebElement to get bounds of
     * @return string array list [from left, from top, width, height]
     */
    List<String> getBoundedRectangleOfElement(WebElement element) {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        JavascriptExecutor je = (JavascriptExecutor) device.getDriver()
        List<String> bounds = (ArrayList<String>) je.executeScript(
                "var rect = arguments[0].getBoundingClientRect();" +
                        "return [ '' + parseInt(rect.left), '' + parseInt(rect.top), '' + parseInt(rect.width), '' + parseInt(rect.height) ]", element)
        bounds
    }

    /**
     * Gets current URL of the web browser
     * @return current URL
     */
    String getCurrentUrl() {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        device.getDriver().getCurrentUrl()
    }

    /**
     * Gets title of the website
     * @return title of web site
     */
    String getTitle() {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        device.getDriver().getTitle()
    }

    /**
     * Navigates back
     */
    void back() {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        device.getDriver().navigate().back()
    }

    /**
     * Navigates forward
     */
    void forward() {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        device.getDriver().navigate().forward()
    }

    /**
     * Refreshes the page
     */
    void refresh() {
        Device device = DeviceManager.getInstance().getActiveDevice(BROWSER)
        device.getDriver().navigate().refresh()
    }

    /**
     * Selects from select box by visible text
     * @param element select box element
     * @param text
     */
    void selectByVisibleText(WebElement element, String text) {
        Select select = new Select(element)
        select.deselectAll()
        select.selectByVisibleText(text)
    }

    /**
     * Selects from select box by value
     * @param element select box element
     * @param value
     */
    void selectByValue(WebElement element, String value) {
        Select select = new Select(element)
        select.deselectAll()
        select.selectByValue(value)
    }

    /**
     * Deselect all from select box
     * @param element - select element
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
     * Checks if element is selected
     *
     * @param element to check
     * @return boolean
     */
    boolean isSelected(WebElement element) {
        element.isSelected()
    }
}
