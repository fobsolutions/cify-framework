package io.cify.framework

import io.cify.framework.core.Device
import io.cify.framework.core.DeviceManager
import io.cify.framework.core.DeviceCategory
import io.cify.framework.matchers.*

import static io.cify.framework.Factory.CAPABILITY_UI_TYPE

/**
 * Created by FOB Solutions
 */
class MatchersTest extends GroovyTestCase {

    void tearDown() {
        DeviceManager.getInstance().quitAllDevices()
    }

    void testGetMatchers() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.ANDROID)
        device.setCapability(CAPABILITY_UI_TYPE, "MobileWeb")
        IMatchers matchers = Matchers.getMatchers()
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersMobileWeb(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetMatchersWithDeviceId() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.ANDROID, 'customDeviceId')
        device.setCapability(CAPABILITY_UI_TYPE, "MobileAndroidApp")
        IMatchers matchers = Matchers.getMatchers("customDeviceId")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersMobileAndroidApp(device).getClass().getName() && matchers instanceof IMatchers
    }


    void testGetMatchersWithDevice() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.IOS)
        device.setCapability(CAPABILITY_UI_TYPE, "MobileIOSApp")
        IMatchers matchers = Matchers.getMatchers(device)
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersMobileIOSApp(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetCustomMatchersWithClassName() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.BROWSER)
        device.setCapability(CAPABILITY_UI_TYPE, "DesktopWeb")
        IMatchers matchers = Matchers.getCustomMatchers("io.cify.framework.matchers.Matchers")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersDesktopWeb(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetCustomMatchersWithIdAndClassName() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.ANDROID, 'customDeviceId')
        device.setCapability(CAPABILITY_UI_TYPE, "TabletAndroidApp")
        IMatchers matchers = Matchers.getCustomMatchers("customDeviceId", "io.cify.framework.matchers.Matchers")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersTabletAndroidApp(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetCustomMatchersWithDeviceAndClassName() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.IOS)
        device.setCapability(CAPABILITY_UI_TYPE, "TabletIOSApp")
        IMatchers matchers = Matchers.getCustomMatchers(device, "io.cify.framework.matchers.Matchers")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersTabletIOSApp(device).getClass().getName() && matchers instanceof IMatchers
    }

    void testGetCustomMatchersByUIType() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.BROWSER)
        device.setCapability(CAPABILITY_UI_TYPE, "TabletWeb")
        IMatchers matchers = Matchers.getCustomMatchers(device, "io.cify.framework.matchers.Matchers")
        String matchersClassName = getProxyObjectClassName(matchers)
        assert matchersClassName == new MatchersTabletWeb(device).getClass().getName() && matchers instanceof IMatchers
    }

    static String getProxyObjectClassName(Object proxyObject) {
        String obj = proxyObject.toString()
        return obj.substring(0, obj.indexOf('@'))
    }

}