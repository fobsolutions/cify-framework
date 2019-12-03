package io.cify.framework.core

import io.cify.common.DeviceCategory
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * Created by FOB Solutions
 */
class DeviceTest extends GroovyTestCase {

    private String androidDeviceId = 'android'
    private String iOSDeviceId = 'iOS'
    private String browserDeviceId = 'browser'

    private DesiredCapabilities androidCaps = new DesiredCapabilities(["version": "6.0"])
    private DesiredCapabilities iOSCaps = new DesiredCapabilities(["version": "9.3.5"])
    private DesiredCapabilities browserCaps = new DesiredCapabilities(["category": "chrome"])

    private Device androidDevice
    private Device iOSDevice
    private Device browserDevice

    void setUp() {
        androidDevice = new Device(androidDeviceId, DeviceCategory.ANDROID, androidCaps)
        iOSDevice = new Device(iOSDeviceId, DeviceCategory.IOS, iOSCaps)
        browserDevice = new Device(browserDeviceId, DeviceCategory.BROWSER, browserCaps)
    }

    void testShouldHaveCorrectId() {
        assert androidDevice.getId().is(androidDeviceId)
    }

    void testShouldHaveCorrectCategory() {
        assert iOSDevice.getCategory().is(DeviceCategory.IOS)
    }

    void testShouldHaveCorrectCapabilities() {
        DesiredCapabilities
        assert browserDevice.getCapabilities().is(browserCaps)
    }

    void testShouldHaveCorrectCapability() {
        browserDevice.setCapability('version', '52.0')
        assert browserDevice.getCapabilities().getCapability('version').is('52.0')
    }

    void testShouldFailToOpenAppWithoutParams() {
        shouldFail {
            androidDevice.openApp()
        }
    }

    void testShouldFailToOpenEmptyAppOnDevice() {
        String appName = ""

        shouldFail {
            androidDevice.openApp(appName)
        }
    }

    void testShouldFailToOpenAppOnBrowserDevice() {
        String appName = "app.apk"

        shouldFail {
            browserDevice.openApp(appName)
        }
    }

    void testShouldFailToOpenApkOnIOSDevice() {
        String appName = "app.apk"

        shouldFail {
            iOSDevice.openApp(appName)
        }
    }

    void testShouldFailToOpenIpaOnAndroidDevice() {
        String appName = "app.ipa"

        shouldFail {
            androidDevice.openApp(appName)
        }
    }

    void testShouldFailToOpenAppOnAndroidDevice() {
        String appName = "app.app"

        shouldFail {
            androidDevice.openApp(appName)
        }
    }

    void testShouldFailToOpenBrowserOnAndroidDevice() {
        String url = "http://www.fob-solutions.com"

        shouldFail {
            androidDevice.openBrowser(url)
        }
    }

    void testShouldFailToOpenBrowserOnIOSDevice() {
        String url = "http://www.fob-solutions.com"

        shouldFail {
            iOSDevice.openBrowser(url)
        }
    }

    void testShouldFailToOpenEmptyUrlInBrowser() {
        String url = ""

        shouldFail {
            browserDevice.openBrowser(url)
        }
    }

    void testHasDriver() {
        assert !iOSDevice.hasDriver()
    }

    void tearDown() {
        androidDevice.quit()
        iOSDevice.quit()
        browserDevice.quit()
    }
}
