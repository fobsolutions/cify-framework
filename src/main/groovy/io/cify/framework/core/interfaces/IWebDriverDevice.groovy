package io.cify.framework.core.interfaces

import org.openqa.selenium.WebDriver

/**
 * Created by FOB Solutions
 */
interface IWebDriverDevice extends ICustomDevice {

    /**
     * Gets device driver
     * */
    WebDriver getDriver()

    /**
     * Open application
     * */
    void openApp()

    /**
     * Open application
     * @param app - app path
     */
    void openApp(String app)

    /**
     * Open application
     * @param app - app path
     * @param appActivity - activity
     * @param appPackage - package
     */
    void openApp(String app, String appActivity, String appPackage)

    /**
     * Opens browser with url
     * @param url
     */
    void openBrowser(String url)

    /**
     * Starts recording
     */
    void startRecording()

    /**
     * Stops recording
     */
    void stopRecording()
}