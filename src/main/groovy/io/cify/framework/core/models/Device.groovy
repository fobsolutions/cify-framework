package io.cify.framework.core.models

import io.cify.framework.core.DriverFactory
import io.cify.framework.Constants
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.codehaus.groovy.GroovyException
import org.openqa.selenium.WebDriver

/**
 * Created by FOB Solutions
 *
 * This is a model class for Device
 */
class Device {


    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private WebDriver driver
    private Map<String, String> capabilities

    /**
     * Constructor for Device model
     * */
    public Device(Map<String, String> capabilities) {
        if (capabilities == null)
            throw new GroovyException("Capabilities cannot be empty!")
        this.capabilities = capabilities
    }

    /**
     * Gets driver from Device
     *
     * @return WebDriver
     * */
    public WebDriver getDriver() {
        return driver
    }

    /**
     * Set device driver
     *
     * @param WebDriver
     * */
    private void setDriver(WebDriver driver) {
        this.driver = driver
    }

    /**
     * quit device driver
     * */
    public void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit()
        }
    }

    /**
     * Create webdriver for device
     * if driver already exists, then quit driver and create new
     * */
    private void createDriver() {
        quitDriver()
        WebDriver driver = DriverFactory.getDriver(getCapabilities())
        setDriver(driver)
    }

    /**
     * Open device app
     *
     * @param app
     * @param appActivity
     * @param appPackage
     * */
    public void openApp(String app, String appActivity, String appPackage) {
        try {
            setCapability("app", app)
            setCapability("app_activity", appActivity)
            setCapability("app_package", appPackage)
            createDriver()
        } catch (all) {
            LOG.error("Failed to open app. With capabilities: " + getCapabilities())
            throw all
        }
    }

    /**
     * Open device app
     *
     * @param app
     * */
    public void openApp(String app) {
        openApp(app, "", "");
    }
    /**
     * Open device browser basing on url
     *
     * @param url
     * */
    public void openBrowser(String url) {
        try {
            createDriver()
            getDriver().get(url)
        } catch (all) {
            LOG.error("Failed to open browser. With capabilities: " + getCapabilities())
            throw all
        }
    }

    /**
     * Gets desired capabilities for device
     *
     * @retun DesiredCapabilities
     * */
    public Map<String, String> getCapabilities() {
        return capabilities
    }

    /**
     * Gets capability parameter
     *
     * @return capability string
     * */
    public String getCapability() {
        return getCapabilityByName(Constants.CAPABILITY)
    }

    /**
     * Gets capability parameter by capability name
     *
     * @return capability string
     * */
    public String getCapabilityByName(String capabilityName) {
        return capabilities[capabilityName]
    }

    /**
     * Set capability parameter
     *
     * @param capability name
     * @param value
     * */
    public void setCapability(String capabilityName, String value) {
        if (capabilityName != null && value != null)
            capabilities.put(capabilityName, value)
    }
}
