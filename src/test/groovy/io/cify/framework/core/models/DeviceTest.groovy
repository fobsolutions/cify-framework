package io.cify.framework.core.models

import io.cify.framework.core.DeviceManager

/**
 * Created by FOB Solutions
 */
class DeviceTest extends GroovyTestCase {

    void tearDown(){
        DeviceManager.quitDevices()
    }

    void testConstructorWithNull() {
        shouldFail {
            new Device(null)
        }
    }

    void testConstructor() {
        Device device = new Device(["capability": "chrome"])
        assert device.getCapability() == "chrome"
    }

    void testGetDriver() {
        Device device = new Device(["capability": "chrome"])
        assert device.getDriver() == null
    }

    void testQuitDriver() {
        Device device = new Device(["capability": "chrome"])
        assert device.quitDriver() == null
    }

    void testGetCapability() {
        Device device = new Device(["capability": "chrome"])
        assert device.getCapability() == "chrome"
    }

    void testGetWrongCapability() {
        Device device = new Device(["capability": "chrome"])
        device.setCapability("someCapability", "12345")
        assert device.getCapabilityByName("wrongCapability") != "12345"
    }

    void testSetCapability() {
        Device device = new Device(["capability": "chrome"])
        device.setCapability("someCapability", "12345")
        assert device.getCapabilityByName("someCapability") == "12345"
    }

    void testSetCapabilityWithNullValue() {
        Device device = new Device(["capability": "chrome"])
        device.setCapability("capability", null)
        assert device.getCapabilityByName("capability") == "chrome"
    }

    void testSetCapabilityWithNullKeyValue() {
        Device device = new Device(["capability": "chrome"])
        device.setCapability(null, null)
        assert device.getCapabilityByName("capability") == "chrome"
    }

    void testDeviceGetCapabilityByName() {
        Device device = new Device(["capability": "chrome"])
        assert device.getCapabilityByName("capability") == "chrome"
    }

    void testDeviceGetCapabilityByNameWithNull() {
        Device device = new Device(["capability": "chrome"])
        assert device.getCapabilityByName(null) != "chrome"
    }

    void testDeviceGetCapabilities() {
        Map<String, String> map = ["capability": "chrome","platform": "android"]
        Device device = new Device(map)
        assert device.getCapabilities() == map
    }

}
