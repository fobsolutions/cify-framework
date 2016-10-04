package io.cify.framework

import io.cify.framework.actions.*
import io.cify.framework.core.Device
import io.cify.framework.core.DeviceManager
import io.cify.framework.core.DeviceCategory

import static io.cify.framework.Factory.CAPABILITY_UI_TYPE

/**
 * Created by FOB Solutions
 */
class ActionsTest extends GroovyTestCase {


    void tearDown() {
        DeviceManager.getInstance().quitAllDevices()
    }

    void testGetActions() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.ANDROID)
        device.setCapability(CAPABILITY_UI_TYPE, "MobileWeb")
        IActions actions = Actions.getActions()
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsMobileWeb(device).getClass().getName() && actions instanceof IActions
    }

    void testGetActionsWithDeviceId() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.ANDROID, 'customDeviceId')
        device.setCapability(CAPABILITY_UI_TYPE, "MobileAndroidApp")
        IActions actions = Actions.getActions("customDeviceId")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsMobileAndroidApp(device).getClass().getName() && actions instanceof IActions
    }


    void testGetActionsWithDevice() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.IOS)
        device.setCapability(CAPABILITY_UI_TYPE, "MobileIOSApp")
        IActions actions = Actions.getActions(device)
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsMobileIOSApp(device).getClass().getName() && actions instanceof IActions
    }

    void testGetCustomActionsWithClassName() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.BROWSER)
        device.setCapability(CAPABILITY_UI_TYPE, "DesktopWeb")
        IActions actions = Actions.getCustomActions("io.cify.framework.actions.Actions")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsDesktopWeb(device).getClass().getName() && actions instanceof IActions
    }

    void testGetCustomActionsWithIdAndClassName() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.BROWSER, 'customDeviceId')
        device.setCapability(CAPABILITY_UI_TYPE, "TabletAndroidApp")
        IActions actions = Actions.getCustomActions("customDeviceId", "io.cify.framework.actions.Actions")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsTabletAndroidApp(device).getClass().getName() && actions instanceof IActions
    }

    void testGetCustomActionsWithDeviceAndClassName() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.IOS)
        device.setCapability(CAPABILITY_UI_TYPE, "TabletIOSApp")
        IActions actions = Actions.getCustomActions(device, "io.cify.framework.actions.Actions")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsTabletIOSApp(device).getClass().getName() && actions instanceof IActions
    }

    void testGetCustomActionsByUIType() {
        Device device = DeviceManager.getInstance().createDevice(DeviceCategory.ANDROID)
        device.setCapability(CAPABILITY_UI_TYPE, "TabletWeb")
        IActions actions = Actions.getCustomActions(device, "io.cify.framework.actions.Actions")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsTabletWeb(device).getClass().getName() && actions instanceof IActions
    }

    static String getProxyObjectClassName(Object proxyObject) {
        String obj = proxyObject.toString()
        return obj.substring(0, obj.indexOf('@'))
    }
}