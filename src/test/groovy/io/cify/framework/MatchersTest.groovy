package io.cify.framework

import io.cify.framework.core.DeviceManager
import io.cify.framework.core.models.Device
import io.cify.framework.matchers.*

/**
 * Created by FOB Solutions
 */
class MatchersTest extends GroovyTestCase {

    void tearDown() {
        DeviceManager.quitDevices()
    }

    void testGetMatchers() {
        Device device = DeviceManager.createDevice(["capability": "ANDROID"])
        device.setCapability(Constants.UI_TYPE, "MobileWeb")
        IMatchers matchers = Matchers.getMatchers()
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersMobileWeb(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetMatchersWithDeviceId() {
        Device device = DeviceManager.createDevice("customDeviceId", ["capability": "ANDROID"])
        device.setCapability(Constants.UI_TYPE, "MobileAndroidApp")
        IMatchers matchers = Matchers.getMatchers("customDeviceId")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersMobileAndroidApp(device).getClass().getName() && matchers instanceof IMatchers
    }


    void testGetMatchersWithDevice() {
        Device device = DeviceManager.createDevice(["capability": "IPHONE"])
        device.setCapability(Constants.UI_TYPE, "MobileIOSApp")
        IMatchers matchers = Matchers.getMatchers(device)
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersMobileIOSApp(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetCustomMatchersWithClassName() {
        Device device = DeviceManager.createDevice(["capability": "SAFARI"])
        device.setCapability(Constants.UI_TYPE, "DesktopWeb")
        IMatchers matchers = Matchers.getCustomMatchers("io.cify.framework.matchers.Matchers")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersDesktopWeb(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetCustomMatchersWithIdAndClassName() {
        Device device = DeviceManager.createDevice("customDeviceId", ["capability": "CHROME"])
        device.setCapability(Constants.UI_TYPE, "TabletAndroidApp")
        IMatchers matchers = Matchers.getCustomMatchers("customDeviceId", "io.cify.framework.matchers.Matchers")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersTabletAndroidApp(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetCustomMatchersWithDeviceAndClassName() {
        Device device = DeviceManager.createDevice(["capability": "SAFARI"])
        device.setCapability(Constants.UI_TYPE, "TabletIOSApp")
        IMatchers matchers = Matchers.getCustomMatchers(device, "io.cify.framework.matchers.Matchers")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersTabletIOSApp(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetCustomMatchersByUIType() {
        Device device = DeviceManager.createDevice(["capability": "SAFARI"])
        device.setCapability(Constants.UI_TYPE, "TabletWeb")
        IMatchers matchers = Matchers.getCustomMatchers(device, "io.cify.framework.matchers.Matchers")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersTabletWeb(device).getClass().getName() && matchers instanceof IMatchers
    }

    static String getProxyObjectClassName(Object proxyObject) {
        String obj = proxyObject.toString()
        return obj.substring(0, obj.indexOf('@'))
    }

}