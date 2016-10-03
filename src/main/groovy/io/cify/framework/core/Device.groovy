package io.cify.framework.core

import io.cify.framework.core.interfaces.IDevice
import io.cify.framework.logging.LoggingOutputStream
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import groovy.util.logging.Slf4j
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * Created by FOB Solutions
 *
 * This is a model class for Device
 */

@Slf4j
class Device implements IDevice {

    private static final Marker MARKER = MarkerFactory.getMarker('DEVICE') as Marker

    private String id
    private DeviceCategory category
    private DesiredCapabilities capabilities

    private WebDriver driver

    /**
     * Default constructor for Device
     *
     * @param id unique device id
     * @param category device category
     * @param capabilities device desired capabilities
     * */
    public Device(String id, DeviceCategory category, DesiredCapabilities capabilities) {
        log.debug(MARKER, "Create new device with id $id, category $category and capabilities $capabilities")
        this.id = id
        this.category = category
        this.capabilities = capabilities
    }

    /**
     * Gets id of device
     *
     * @retun device id
     * */
    @Override
    public String getId() {
        log.debug(MARKER, "Get device id")
        return id
    }

    /**
     * Gets category for device
     *
     * @retun device category
     * */
    @Override
    public DeviceCategory getCategory() {
        log.debug(MARKER, "Get device category")
        return this.category
    }

    /**
     * Gets driver from Device
     *
     * @return WebDriver
     * */
    @Override
    public WebDriver getDriver() {
        log.debug(MARKER, "Get device driver")
        return driver
    }

    /**
     * Sets desired capability parameter
     *
     * @param key
     * @param value
     * */
    @Override
    public void setCapability(String key, String value) {
        log.debug(MARKER, "Set desire capability $key : $value")
        if (key != null && value != null) {
            capabilities.setCapability(key, value)
        }
    }


    /**
     * Gets desired capabilities for device
     *
     * @retun DesiredCapabilities
     * */
    @Override
    public DesiredCapabilities getCapabilities() {
        log.debug(MARKER, "Get all desired capabilities")
        return capabilities
    }

    /**
     * Opens app on device
     *
     * @param app
     * */
    @Override
    public void openApp(String app) {
        openApp(app, "", "");
    }


    /**
     * Opens app on device
     *
     * @param app
     * @param appActivity
     * @param appPackage
     *
     * @throws CifyFrameworkException  if failed to open app
     * */
    @Override
    public void openApp(String app, String appActivity, String appPackage) {
        log.debug(MARKER, "Open app $app, $appActivity, $appPackage")
        try {
            if(!validateApp(app, appActivity, appPackage)) {
                throw new CifyFrameworkException("App in not valid")
            }
            setCapability("app", app)
            setCapability("app_activity", appActivity)
            setCapability("app_package", appPackage)
            createDriver()
        } catch (all) {
            throw new CifyFrameworkException("Failed to open app $app, $appActivity, $appPackage", all)
        }
    }


    /**
     * Opens url in device browser
     *
     * @param url
     *
     * @throws CifyFrameworkException  if failed to open url
     * */
    @Override
    public void openBrowser(String url) {
        log.debug(MARKER, "Open url $url")
        try {
            if(!validateUrl(url)){
                throw new CifyFrameworkException("Url in not valid")
            }
            createDriver()
            getDriver().get(url)
        } catch (all) {
            throw new CifyFrameworkException("Failed to open url $url", all)
        }
    }

    /**
     * Quits app or browser
     * */
    @Override
    void quit() {
        if(hasDriver()) {
            log.debug(MARKER, "Quit device driver")
            getDriver().quit()
        }
    }


    /**
     * Creates webdriver for device
     * */
    private void createDriver() {
        log.debug(MARKER, "Create new device driver")
        LoggingOutputStream.redirectSystemOutAndSystemErrToLogger()
        quit()
        WebDriver driver = DriverFactory.getDriver(getCapabilities())
        this.driver = driver
    }

    /**
     * Checks if driver exists
     * */
    private boolean hasDriver() {
        log.debug(MARKER, "Check if driver exists")
        if (getDriver() == null) {
            log.debug(MARKER, "No driver found")
            return false
        }

        log.debug(MARKER, "Driver found")
        return true
    }

    /**
     * Validates app
     *
     * @param app
     * @param appActivity
     * @param appPackage
     *
     * @return boolean
     * */
    private boolean validateApp(String app, String appActivity, String appPackage) {
        if(app == null || app.isEmpty()) {
            return false
        }
        if(getCategory() == DeviceCategory.BROWSER) {
            return false
        }
        if(getCategory() == DeviceCategory.IOS && app.endsWith(".apk")) {
            return false
        }

        if(getCategory() == DeviceCategory.ANDROID && (app.endsWith(".ipa") || app.endsWith(".app"))) {
            return false
        }

        return true
    }


    /**
     * Validates app
     *
     * @param url
     *
     * @return boolean
     * */
    private boolean validateUrl(String url) {
        if(url == null || url.isEmpty()) {
            return false
        }
        if(getCategory() != DeviceCategory.BROWSER) {
            return false
        }

        return true
    }
}
