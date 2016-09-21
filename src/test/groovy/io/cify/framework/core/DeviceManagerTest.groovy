package io.cify.framework.core

import io.cify.framework.core.models.Device
import io.cify.framework.Constants
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * Created by FOB Solutions
 */
class DeviceManagerTest extends GroovyTestCase {

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


    void tearDown() {
        DeviceManager.quitDevices()
    }

    void setUp() {
        testProjectDir.create()
        capabilitiesFile = testProjectDir.newFile(Constants.FULL_CAPABILITIES_FILE)
        capabilitiesFile.write(fourCapabilities)
        System.setProperty("capabilitiesFilePath", capabilitiesFile.getAbsolutePath())
    }

    void testCreateDevice() {
        System.properties.setProperty("mainCapability", "capabilityId:android")
        Device device = DeviceManager.createDevice()
        device.getCapability()
        assert device.getCapability() == "ANDROID"
    }

    void testCreateDeviceWithKeyValue() {
        Device device = DeviceManager.createDevice("capability", "ANDROID")
        device.getCapability()
        assert device.getCapability() == "ANDROID"
    }

    void testCreateDeviceWithCapabilityIgnoreCase() {
        Device device = DeviceManager.createDevice("capability", "AnDrOiD")
        device.getCapability()
        assert device.getCapability() == "ANDROID"
    }

    void testCreateDeviceWithCapability() {
        Device device = DeviceManager.createDevice(["capability": "ANDROID"])
        device.getCapability()
        assert device.getCapability() == "ANDROID"
    }

    void testCreateDeviceWithCustomDeviceId() {
        System.properties.setProperty("mainCapability", "capabilityId:android")
        Device device = DeviceManager.createDevice("customDeviceId")
        device.getCapability()
        assert device.getCapability() == "ANDROID"
    }

    void testCreateDeviceWithCustomDeviceIdAndCapability() {
        Device device = DeviceManager.createDevice("customDeviceId", ["capability": "ANDROID"])
        device.getCapability()
        assert device.getCapability() == "ANDROID"
    }

    void testCreateManyDevices() {
        System.properties.setProperty("mainCapability", "capabilityId:android")
        3.times{ DeviceManager.createDevice() }
        assert DeviceManager.getAllActiveDevices().size() == 3
    }

    void testHasActiveDevice() {
        DeviceManager.createDevice("customDeviceId", ["capability": "ANDROID"])
        assert DeviceManager.hasActiveDevice("customDeviceId")
    }

    void testGetActiveDeviceWhenNoActiveDevices() {
        shouldFail {
            DeviceManager.getActiveDevice()
        }
    }

    void testGetActiveDeviceWithIncorrectID() {
        shouldFail {
            DeviceManager.getActiveDevice("WRONG") != null
        }
    }

    void testGetActiveDevice() {
        DeviceManager.createDevice(["capability": "ANDROID"])
        DeviceManager.createDevice(["capability": "CHROME"])
        DeviceManager.createDevice(["capability": "SAFARI"])
        assert DeviceManager.getActiveDevice().getCapability() == "ANDROID"
    }

    void testGetActiveDeviceWithId() {
        DeviceManager.createDevice("customDeviceId",["capability": "ANDROID"])
        DeviceManager.createDevice("anotherDeviceId",["capability": "CHROME"])
        assert DeviceManager.getActiveDevice("anotherDeviceId").getCapability() == "CHROME"
    }

    void testGetAllActiveDevices(){
        3.times { DeviceManager.createDevice("customDeviceId${it}", ["capability": "ANDROID"]) }
        assert DeviceManager.getAllActiveDevices().size() == 3
    }

    void testQuitDevice() {
        def n = 3
        n.times {
            DeviceManager.createDevice("customDeviceId${it}", ["capability": "ANDROID"])
        }
        boolean deviceActive = false
        n.times {
            DeviceManager.quitDevice("customDeviceId${it}")
            if (DeviceManager.hasActiveDevice("customDeviceId${it}")) {
                deviceActive = true
            }
        }
        assert !deviceActive
    }

    void testQuitDevices() {
        def n = 3
        n.times { DeviceManager.createDevice("customDeviceId${it}", ["capability": "ANDROID"]) }
        DeviceManager.quitDevices()
        boolean deviceActive = false
        n.times {
            if (DeviceManager.hasActiveDevice("customDeviceId${it}")) {
                deviceActive = true
            }
        }
        assert !deviceActive
    }


}
