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

    public static enum Capability {
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
            return createRemoteDriver(capability, desiredCapabilities, new URL(remoteUrl))
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
                ChromeDriverManager.getInstance().setup()
                return new ChromeDriver(desiredCapabilities)
            case Capability.FIREFOX:
                MarionetteDriverManager.getInstance().setup()
                return new FirefoxDriver(desiredCapabilities)
            case Capability.OPERA:
                OperaDriverManager.getInstance().setup()
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
                InternetExplorerDriverManager.getInstance().setup()
                return new InternetExplorerDriver(desiredCapabilities)
            case Capability.EDGE:
                EdgeDriverManager.getInstance().setup()
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
    public static DesiredCapabilities replaceCapabilities(DesiredCapabilities capabilities) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities()
        capabilities.asMap().each { key, value ->

            def capabilityValue

            if (System.getenv(key)) {
                capabilityValue = System.getenv(key)
            } else if (System.getProperty(key)) {
                capabilityValue = System.getProperty(key)
            } else {
                capabilityValue = value
            }
            desiredCapabilities[key] = capabilityValue
        }
        return desiredCapabilities
    }

    /**
     * Creates remote driver for given capabilities
     *
     * @param capability capability
     * @param desiredCapabilities DesiredCapabilities
     * @param url remote url
     * @return WebDriver
     * */
    private
    static WebDriver createRemoteDriver(Capability capability, DesiredCapabilities desiredCapabilities, URL url) {
        LOG.debug(MARKER, "Create $capability remote driver with remote $url and desired capabilities \n $desiredCapabilities")

        desiredCapabilities = mergeCapabilitiesWithDefault(desiredCapabilities)
        desiredCapabilities = replaceCapabilities(desiredCapabilities)

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
