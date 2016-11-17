package io.cify.framework.core

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.events.WebDriverEventListener

import static io.cify.framework.recording.RecordingController.takeScreenshot

/*************************************** PURPOSE **********************************

 - This class implements the WebDriverEventListener, which is included under events.
 The purpose of implementing this interface is to override all the methods and define certain useful  Log statements
 which would be displayed/logged as the application under test is being run.

 Do not call any of these methods, instead these methods will be invoked automatically
 as an when the action done (click, findBy etc).

 */
class RecordingEventListener implements WebDriverEventListener {

    Device device

    RecordingEventListener(Device device) {
        device.isRecording = true
        this.device = device
    }

    /**
     * Called before {@link org.openqa.selenium.WebDriver#get get(String url)} respectively
     * {@link org.openqa.selenium.WebDriver.Navigation#to navigate().to(String url)}.
     *
     * @param url URL
     * @param driver WebDriver
     */
    @Override
    void beforeNavigateTo(String url, WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called after {@link org.openqa.selenium.WebDriver#get get(String url)} respectively
     * {@link org.openqa.selenium.WebDriver.Navigation#to navigate().to(String url)}. Not called, if an
     * exception is thrown.
     *
     * @param url URL
     * @param driver WebDriver
     */
    @Override
    void afterNavigateTo(String url, WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called before {@link org.openqa.selenium.WebDriver.Navigation#back navigate().back()}.
     *
     * @param driver WebDriver
     */
    @Override
    void beforeNavigateBack(WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called after {@link org.openqa.selenium.WebDriver.Navigation navigate().back()}. Not called, if an
     * exception is thrown.
     *
     * @param driver WebDriver
     */
    @Override
    void afterNavigateBack(WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called before {@link org.openqa.selenium.WebDriver.Navigation#forward navigate().forward()}.
     *
     * @param driver WebDriver
     */
    @Override
    void beforeNavigateForward(WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called after {@link org.openqa.selenium.WebDriver.Navigation#forward navigate().forward()}. Not called,
     * if an exception is thrown.
     *
     * @param driver WebDriver
     */
    @Override
    void afterNavigateForward(WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called before {@link org.openqa.selenium.WebDriver.Navigation#refresh navigate().refresh()}.
     *
     * @param driver WebDriver
     */
    @Override
    void beforeNavigateRefresh(WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called after {@link org.openqa.selenium.WebDriver.Navigation#refresh navigate().refresh()}. Not called,
     * if an exception is thrown.
     *
     * @param driver WebDriver
     */
    @Override
    void afterNavigateRefresh(WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called before {@link WebDriver#findElement WebDriver.findElement(...)}, or
     * {@link WebDriver#findElements WebDriver.findElements(...)}, or {@link WebElement#findElement
     * WebElement.findElement(...)}, or {@link WebElement#findElement WebElement.findElements(...)}.
     *
     * @param by locator being used
     * @param element will be <code>null</code>, if a find method of <code>WebDriver</code> is called.
     * @param driver WebDriver
     */
    @Override
    void beforeFindBy(By by, WebElement element, WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called after {@link WebDriver#findElement WebDriver.findElement(...)}, or
     * {@link WebDriver#findElements WebDriver.findElements(...)}, or {@link WebElement#findElement
     * WebElement.findElement(...)}, or {@link WebElement#findElement WebElement.findElements(...)}.
     *
     * @param by locator being used
     * @param element will be <code>null</code>, if a find method of <code>WebDriver</code> is called.
     * @param driver WebDriver
     */
    @Override
    void afterFindBy(By by, WebElement element, WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called before {@link WebElement#click WebElement.click()}.
     *
     * @param element the WebElement being used for the action @param driver WebDriver
     *
     */
    @Override
    void beforeClickOn(WebElement element, WebDriver driver) {
        println("BEFORE CLICK!!!!!!!!")
        takeScreenshot(device)
    }

    /**
     * Called after {@link WebElement#click WebElement.click()}. Not called, if an exception is
     * thrown.
     *
     * @param element the WebElement being used for the action @param driver WebDriver
     *
     */
    @Override
    void afterClickOn(WebElement element, WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called before {@link WebElement#clear WebElement.clear()}, {@link WebElement#sendKeys
     * WebElement.sendKeys(...)}.
     *
     * @param element the WebElement being used for the action @param driver WebDriver
     * @param keysToSend
     */
    @Override
    void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        takeScreenshot(device)
    }

    /**
     * Called after {@link WebElement#clear WebElement.clear()}, {@link WebElement#sendKeys
     * WebElement.sendKeys(...)}}. Not called, if an exception is thrown.
     *
     * @param element the WebElement being used for the action @param driver WebDriver
     * @param keysToSend
     */
    @Override
    void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        takeScreenshot(device)
    }

    /**
     * Called before {@link org.openqa.selenium.remote.RemoteWebDriver#executeScript(java.lang.String, java.lang.Object [ ])}
     *
     * @param script the script to be executed @param driver WebDriver
     *
     */
    @Override
    void beforeScript(String script, WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called after {@link org.openqa.selenium.remote.RemoteWebDriver#executeScript(java.lang.String, java.lang.Object [ ])}.
     * Not called if an exception is thrown
     *
     * @param script the script that was executed @param driver WebDriver
     *
     */
    @Override
    void afterScript(String script, WebDriver driver) {
        takeScreenshot(device)
    }

    /**
     * Called whenever an exception would be thrown.
     *
     * @param throwable the exception that will be thrown @param driver WebDriver
     *
     */
    @Override
    void onException(Throwable throwable, WebDriver driver) {
        takeScreenshot(device)
    }
}
