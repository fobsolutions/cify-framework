package io.cify.framework.core

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.github.bonigarcia.wdm.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.opera.OperaDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.safari.SafariDriver

/**
 * Created by FOB Solutions
 *
 * This class is responsible for driver creation
 */

class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('DRIVER FACTORY') as Marker

    public static final String CAPABILITY = "capability"
    public static final String REMOTE = "remote"

    static enum Capability {
        CHROME,
        SAFARI,
        OPERA,
        FIREFOX,
        ANDROID,
        INTERNETEXPLORER,
        IPAD,
        IPHONE,
        PHANTOMJS,
        EDGE
    }

    /**
     * Creates web driver with desired capabilities
     *
     * @param desiredCapabilities DesiredCapabilities
     *
     * @return WebDriver
     * */
    static WebDriver getDriver(DesiredCapabilities desiredCapabilities) {
        LOG.debug(MARKER, "Create driver with desired capabilities \n $desiredCapabilities")

        Capability capability = Capability.valueOf((desiredCapabilities.getCapability(CAPABILITY) as String).toUpperCase())
        String remoteUrl = desiredCapabilities.getCapability(REMOTE) as String

        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            return createRemoteDriver(capability, desiredCapabilities)
        } else {
            return createWebDriver(capability, desiredCapabilities)
        }
    }

    /**
     * Merge capabilities provided by user with default capabilities
     *
     * @param desiredCapabilities provided capabilities
     *
     * @return DesiredCapabilities
     * */
    private static DesiredCapabilities mergeCapabilitiesWithDefault(DesiredCapabilities desiredCapabilities) {
        Capability capability = Capability.valueOf((desiredCapabilities.getCapability(CAPABILITY) as String).toUpperCase())

        switch (capability) {
            case Capability.CHROME:
                return DesiredCapabilities.chrome().merge(desiredCapabilities)
            case Capability.FIREFOX:
                return DesiredCapabilities.firefox().merge(desiredCapabilities)
            case Capability.OPERA:
                return DesiredCapabilities.operaBlink().merge(desiredCapabilities)
            case Capability.SAFARI:
                return DesiredCapabilities.safari().merge(desiredCapabilities)
            case Capability.ANDROID:
                return DesiredCapabilities.android().merge(desiredCapabilities)
            case Capability.IPHONE:
                return DesiredCapabilities.iphone().merge(desiredCapabilities)
            case Capability.IPAD:
                return DesiredCapabilities.ipad().merge(desiredCapabilities)
            case Capability.INTERNETEXPLORER:
                return DesiredCapabilities.internetExplorer().merge(desiredCapabilities)
            case Capability.EDGE:
                return DesiredCapabilities.edge().merge(desiredCapabilities)
            default:
                throw new CifyFrameworkException("Not supported capability $capability")
        }
    }

    /**
     * Creates web driver for given capabilities
     *
     * @param capability capability
     * @param desiredCapabilities DesiredCapabilities
     * @return WebDriver
     * */
    private static WebDriver createWebDriver(Capability capability, DesiredCapabilities desiredCapabilities) {
        LOG.debug(MARKER, "Create $capability web driver with desired capabilities $desiredCapabilities")

        desiredCapabilities = mergeCapabilitiesWithDefault(desiredCapabilities)
        desiredCapabilities = replaceCapabilities(desiredCapabilities)

        switch (capability) {
            case Capability.CHROME:
                WebDriverManager.chromedriver().setup()
                return new ChromeDriver(desiredCapabilities)
            case Capability.FIREFOX:
                WebDriverManager.firefoxdriver().setup()
                return new FirefoxDriver(desiredCapabilities)
            case Capability.OPERA:
                WebDriverManager.operadriver().setup()
                return new OperaDriver(desiredCapabilities)
            case Capability.SAFARI:
                return new SafariDriver(desiredCapabilities)
            case Capability.ANDROID:
                return new AndroidDriver(desiredCapabilities)
            case Capability.IPHONE:
                return new IOSDriver(desiredCapabilities)
            case Capability.IPAD:
                return new IOSDriver(desiredCapabilities)
            case Capability.INTERNETEXPLORER:
                WebDriverManager.iedriver().setup()
                return new InternetExplorerDriver(desiredCapabilities)
            case Capability.EDGE:
                WebDriverManager.edgedriver().setup()
                return new EdgeDriver(desiredCapabilities)
            default:
                throw new CifyFrameworkException("Not supported web driver capability $capability")
        }
    }

    /**
     * Replaces capabilities values with environment and properties values
     *
     * @param capabilities DesiredCapabilities
     *
     * @retun DesiredCapabilities
     * */
    private static DesiredCapabilities replaceCapabilities(DesiredCapabilities capabilities) {
        capabilities.asMap().each { key, value ->
            List<String> replaceList = value.toString().findAll(/<replaceProperty:(.*?)>/)
            replaceList.each {
                value = value.toString().replace(it, getPropertyValue(it))
                capabilities.setCapability(key, value)
            }
        }
        return capabilities
    }

    /**
     * Gets property value from environment or system properties
     *
     * @param property
     *
     * @return String
     * */
    private static String getPropertyValue(String property) {

        String value = property.toString().replace("<replaceProperty:", "").replace(">", "")

        if (System.getenv(value)) {
            return System.getenv(value)
        } else if (System.getProperty(value)) {
            return System.getProperty(value)
        } else {
            throw new CifyFrameworkException("Cannot find property named $value from System properties or Environment variables")
        }
    }

    /**
     * Creates remote driver for given capabilities
     *
     * @param capability capability
     * @param desiredCapabilities DesiredCapabilities
     * @return WebDriver
     * */
    private
    static WebDriver createRemoteDriver(Capability capability, DesiredCapabilities desiredCapabilities) {
        LOG.debug(MARKER, "Create $capability remote driver desired capabilities \n $desiredCapabilities")

        desiredCapabilities = mergeCapabilitiesWithDefault(desiredCapabilities)
        desiredCapabilities = replaceCapabilities(desiredCapabilities)

        String remoteUrl = desiredCapabilities.getCapability(REMOTE) as String
        URL url = new URL(remoteUrl)

        switch (capability) {
            case Capability.IPAD:
            case Capability.IPHONE:
                return new IOSDriver<>(url, desiredCapabilities)
            case Capability.ANDROID:
                return new AndroidDriver<>(url, desiredCapabilities)
            default:
                return new RemoteWebDriver(url, desiredCapabilities)
        }
    }
}
