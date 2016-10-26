package io.cify.framework.core.interfaces

import io.cify.framework.core.DeviceCategory
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * Created by FOB Solutions
 */
interface IDevice {

    String getId()

    DeviceCategory getCategory()

    WebDriver getDriver()

    void setCapability(String key, String value)

    DesiredCapabilities getCapabilities()

    void openApp()

    void openApp(String app)

    void openApp(String app, String appActivity, String appPackage)

    void openBrowser(String url)

    void startRecording()

    void stopRecording()

    void quit()
}