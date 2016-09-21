package io.cify.framework

import io.cify.framework.actions.*
import io.cify.framework.core.DeviceManager
import io.cify.framework.core.models.Device
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * Created by FOB Solutions
 */
class ActionsTest extends GroovyTestCase {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File capabilitiesFile

    String fourCapabilities = "{\n" +
            "    \"capabilities\": [\n" +
            "        {\n" +
            "            \"capability\": \"CHROME\",\n" +
            "            \"capabilityId\": \"chrome\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"SAFARI\",\n" +
            "            \"capabilityId\": \"safari\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"OPERA\",\n" +
            "            \"capabilityId\": \"opera\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"FIREFOX\",\n" +
            "            \"capabilityId\": \"firefox\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"ANDROID\",\n" +
            "            \"capabilityId\": \"android\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"INTERNETEXPLORER\",\n" +
            "            \"capabilityId\": \"internetexplorer\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"IPAD\",\n" +
            "            \"capabilityId\": \"ipad\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"IPHONE\",\n" +
            "            \"capabilityId\": \"iphone\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"PHANTOMJS\",\n" +
            "            \"capabilityId\": \"phantomjs\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"capability\": \"EDGE\",\n" +
            "            \"capabilityId\": \"edge\"\n" +
            "        }\n" +
            "    ]\n" +
            "}"

    void setUp() {
        testProjectDir.create()
        capabilitiesFile = testProjectDir.newFile(Constants.FULL_CAPABILITIES_FILE)
        capabilitiesFile.write(fourCapabilities)
        System.setProperty("capabilitiesFilePath", capabilitiesFile.getAbsolutePath())
    }

    void tearDown() {
        DeviceManager.quitDevices()
    }

    void testGetActions() {
        Device device = DeviceManager.createDevice(["capability": "ANDROID"])
        device.setCapability(Constants.UI_TYPE, "MobileWeb")
        IActions actions = Actions.getActions()
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsMobileWeb(device).getClass().getName() && actions instanceof IActions
    }

    void testGetActionsWithDeviceId() {
        Device device = DeviceManager.createDevice("customDeviceId", ["capability": "ANDROID"])
        device.setCapability(Constants.UI_TYPE, "MobileAndroidApp")
        IActions actions = Actions.getActions("customDeviceId")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsMobileAndroidApp(device).getClass().getName() && actions instanceof IActions
    }


    void testGetActionsWithDevice() {
        Device device = DeviceManager.createDevice(["capability": "IPHONE"])
        device.setCapability(Constants.UI_TYPE, "MobileIOSApp")
        IActions actions = Actions.getActions(device)
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsMobileIOSApp(device).getClass().getName() && actions instanceof IActions
    }

    void testGetCustomActionsWithClassName() {
        Device device = DeviceManager.createDevice(["capability": "SAFARI"])
        device.setCapability(Constants.UI_TYPE, "DesktopWeb")
        IActions actions = Actions.getCustomActions("io.cify.framework.actions.Actions")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsDesktopWeb(device).getClass().getName() && actions instanceof IActions
    }

    void testGetCustomActionsWithIdAndClassName() {
        Device device = DeviceManager.createDevice("customDeviceId", ["capability": "CHROME"])
        device.setCapability(Constants.UI_TYPE, "TabletAndroidApp")
        IActions actions = Actions.getCustomActions("customDeviceId", "io.cify.framework.actions.Actions")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsTabletAndroidApp(device).getClass().getName() && actions instanceof IActions
    }

    void testGetCustomActionsWithDeviceAndClassName() {
        Device device = DeviceManager.createDevice(["capability": "SAFARI"])
        device.setCapability(Constants.UI_TYPE, "TabletIOSApp")
        IActions actions = Actions.getCustomActions(device, "io.cify.framework.actions.Actions")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsTabletIOSApp(device).getClass().getName() && actions instanceof IActions
    }

    void testGetCustomActionsByUIType() {
        Device device = DeviceManager.createDevice(["capability": "SAFARI"])
        device.setCapability(Constants.UI_TYPE, "TabletWeb")
        IActions actions = Actions.getCustomActions(device, "io.cify.framework.actions.Actions")
        String actionsClassName = getProxyObjectClassName(actions)
        assert actionsClassName == new ActionsTabletWeb(device).getClass().getName() && actions instanceof IActions
    }

    static String getProxyObjectClassName(Object proxyObject) {
        String obj = proxyObject.toString()
        return obj.substring(0, obj.indexOf('@'))
    }
}