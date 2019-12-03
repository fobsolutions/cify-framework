package io.cify.framework.core.interfaces

import io.cify.common.DeviceCategory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * Created by FOB Solutions
 */
interface IDevice {

    /**
     * Gets device ID
     * */
    String getId()

    /**
     * Gets device category
     * */
    DeviceCategory getCategory()

    /**
     * Gets device driver
     * */
    WebDriver getDriver()

    /**
     * Sets capability to device
     * */
    void setCapability(String key, String value)

    /**
     * Gets device capabilities
     * @return DesiredCapability
     */
    DesiredCapabilities getCapabilities()

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

    /**
     * Closes the device
     */
    void quit()
}