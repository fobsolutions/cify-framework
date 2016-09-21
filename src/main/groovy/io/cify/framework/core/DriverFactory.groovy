package io.cify.framework.core

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import io.github.bonigarcia.wdm.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.codehaus.groovy.GroovyException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.opera.OperaDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.safari.SafariDriver

import static io.cify.framework.Constants.*

/**
 * Created by FOB Solutions
 *
 * This class is responsible for driver creation
 */

class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger;

    /**
     * Creates web driver with specified type of RemoteWebDriver
     *
     * @param desiredCapabilities - DesiredCapabilities
     * @return WebDriver
     * */
    public static WebDriver getDriver(Map<String, String> capabilities) {
        DesiredCapabilities desiredCapabilities = convertToDesiredCapabilities(capabilities)
        String remoteUrl = desiredCapabilities.getCapability(REMOTE) as String

        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            LOG.debug("Creating new remote driver with URL:" + remoteUrl)
            return createRemoteDriver(desiredCapabilities, new URL(remoteUrl))
        } else {
            WebDriver driver
            try {
                LOG.debug("Creating new web driver without remote url")
                driver = createWebDriver(desiredCapabilities)
            } catch (all) {
                throw new GroovyException("Cannot create web driver with capabilities: " + desiredCapabilities + " cause: " + all.message, all)
            }
            LOG.debug("Driver created.")
            return driver
        }
    }

    /**
     * Converts capabilities to DesiredCapabilities
     *
     * @param capabilities - Map<String, String> of capabilities
     * @return DesiredCapabilities
     * */
    private static DesiredCapabilities convertToDesiredCapabilities(Map<String, String> capabilities) {
        DesiredCapabilities cap = getCapability(capabilities.get(CAPABILITY) as String)
        capabilities.each { k, v ->
            cap.setCapability(k, v)
        }
        return cap
    }

    /**
     * return DesiredCapabilities for given capability string
     *
     * @param String capability
     * @return DesiredCapabilities
     * */
    private static DesiredCapabilities getCapability(String capability) {

        if (capability == null || capability.isEmpty()) {
            throw new NoSuchFieldError("Cannot find capability with empty capability parameter")
        }

        Capabilities cap = Capabilities.valueOf(capability.toUpperCase())
        switch (cap) {
            case Capabilities.CHROME:
                return DesiredCapabilities.chrome()
            case Capabilities.FIREFOX:
                return DesiredCapabilities.firefox()
            case Capabilities.OPERA:
                return DesiredCapabilities.operaBlink()
            case Capabilities.SAFARI:
                return DesiredCapabilities.safari()
            case Capabilities.ANDROID:
                return DesiredCapabilities.android()
            case Capabilities.IPHONE:
                return DesiredCapabilities.iphone()
            case Capabilities.IPAD:
                return DesiredCapabilities.ipad()
            case Capabilities.INTERNETEXPLORER:
                return DesiredCapabilities.internetExplorer()
            case Capabilities.PHANTOMJS:
                return DesiredCapabilities.phantomjs()
            case Capabilities.EDGE:
                return DesiredCapabilities.edge()
            default:
                throw new GroovyException("Cannot find capability with name " + capability)
        }
    }

    /**
     * Gets WebDriver for specified capabilities
     *
     * @param capabilities
     * @return returns driver
     * */
    private static WebDriver createWebDriver(DesiredCapabilities capabilities) {
        Capabilities cap = Capabilities.valueOf((capabilities.getCapability(CAPABILITY) as String).toUpperCase())
        switch (cap) {
            case Capabilities.CHROME:
                ChromeDriverManager.getInstance().setup()
                return new ChromeDriver(capabilities)
            case Capabilities.FIREFOX:
                MarionetteDriverManager.getInstance().setup()
                return new FirefoxDriver(capabilities)
            case Capabilities.OPERA:
                OperaDriverManager.getInstance().setup()
                return new OperaDriver(capabilities)
            case Capabilities.SAFARI:
                return new SafariDriver(capabilities)
            case Capabilities.ANDROID:
                return new AndroidDriver(capabilities)
            case Capabilities.IPHONE:
                return new IOSDriver(capabilities)
            case Capabilities.IPAD:
                return new IOSDriver(capabilities)
            case Capabilities.INTERNETEXPLORER:
                InternetExplorerDriverManager.getInstance().setup()
                return new InternetExplorerDriver(capabilities)
            case Capabilities.PHANTOMJS:
                PhantomJsDriverManager.getInstance().setup()
                String remoteUrl = capabilities.getCapability(REMOTE) as String
                if (remoteUrl == null || remoteUrl.isEmpty()) throw new WebDriverException("Cannot create web driver with " + cap + " cause REMOTE URL is not set")
                return new RemoteWebDriver(new URL(remoteUrl), capabilities)
            case Capabilities.EDGE:
                EdgeDriverManager.getInstance().setup()
                return new EdgeDriver(capabilities)
            default:
                throw new GroovyException("Cannot find capability with name " + cap)
        }
    }

    /**
     * Creates remote driver for given capability
     *
     * @param capability - DesiredCapability
     * @param url - remote url
     * @returns WebDriver
     * */
    private static WebDriver createRemoteDriver(DesiredCapabilities capability, URL url) {
        Capabilities cap = Capabilities.valueOf((capability.getCapability(CAPABILITY) as String).toUpperCase())
        switch (cap) {
            case Capabilities.IPAD:
            case Capabilities.IPHONE:
                return new IOSDriver<>(url, capability)
            case Capabilities.ANDROID:
                return new AndroidDriver<>(url, capability)
            default:
                return new RemoteWebDriver(url, capability)

        }

    }
}
