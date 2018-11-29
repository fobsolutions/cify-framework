package io.cify.framework.core

import groovy.json.JsonSlurper
import org.openqa.selenium.remote.DesiredCapabilities

class DeviceManagerTest extends GroovyTestCase {


    private String caps = '''{
        "android": {
           "version": ""
        },
        "browser": {
           "category": "chrome"
        },
        "ios": {
           "version": ""
        }
    }'''

    private String capsWithoutAndroid = '''{
        "browser" : {
            "category" : "chrome",
        },
        "ios" : {
            "version" : ""
        }
    }'''

    private String capsBadJson = '!!!!!!'

    void testAddToDesiredCapabilities() {
        System.setProperty(DeviceManager.SYSTEM_PROPERTY_CAPABILITIES, caps)

        Capabilities capabilities = new DeviceManager().getCapabilities()
        capabilities.addToDesiredCapabilities(DeviceCategory.BROWSER, "test", "testValue")

        assert capabilities.toDesiredCapabilities(DeviceCategory.BROWSER).getCapability("test") == "testValue"
    }

    void testAddObjectToDesiredCapabilities() {
        System.setProperty(DeviceManager.SYSTEM_PROPERTY_CAPABILITIES, caps)

        Capabilities capabilities = new DeviceManager().getCapabilities()
        WebDriverDevice device = new WebDriverDevice("id", DeviceCategory.ANDROID, new DesiredCapabilities().setCapability("asd", "asd"))
        capabilities.addToDesiredCapabilities(DeviceCategory.ANDROID, "test", device)

        assert capabilities.toDesiredCapabilities(DeviceCategory.ANDROID).getCapability("test") == device
    }

    void testShouldUseProvidedCapabilities() {
        System.setProperty(DeviceManager.SYSTEM_PROPERTY_CAPABILITIES, caps)

        Capabilities capabilities = new DeviceManager().getCapabilities()
        Capabilities systemProperties = new JsonSlurper().parseText(caps) as Capabilities

        assert capabilities.toPrettyString() == systemProperties.toPrettyString()
    }

    void testShouldFailUsingIncorrectCapabilities() {
        System.setProperty(DeviceManager.SYSTEM_PROPERTY_CAPABILITIES, capsBadJson)

        shouldFail {
            new DeviceManager()
        }
    }


    void testShouldCreateDeviceManagerInstance() {
        assert DeviceManager.getInstance() != null
    }


    void testShouldUseAlreadyCreatedDeviceManagerInstance() {
        DeviceManager deviceManager = DeviceManager.getInstance()

        assert DeviceManager.getInstance().is(deviceManager)
    }


    void testShouldCreateAndroidDevice() {
        DeviceCategory category = DeviceCategory.ANDROID

        WebDriverDevice device = new DeviceManager().createWebDriverDevice(category)
        assert device.getCategory().is(category)
    }


    void testShouldCreateIOSDevice() {
        DeviceCategory category = DeviceCategory.IOS

        WebDriverDevice device = new DeviceManager().createWebDriverDevice(category)
        assert device.getCategory().is(category)
    }


    void testShouldCreateBrowserDevice() {
        DeviceCategory category = DeviceCategory.BROWSER

        WebDriverDevice device = new DeviceManager().createWebDriverDevice(category)
        assert device.getCategory().is(category)
    }


    void testShouldCreateDeviceWithId() {
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = "phone"

        WebDriverDevice device = new DeviceManager().createWebDriverDevice(category, deviceId)
        assert device.getId().is(deviceId)
    }


    void testShouldCreateMultipleUniqueDevicesWithSameCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId0 = "phone-0"
        String deviceId1 = "phone-1"

        WebDriverDevice device0 = deviceManager.createWebDriverDevice(category, deviceId0)
        WebDriverDevice device1 = deviceManager.createWebDriverDevice(category, deviceId1)

        assert device0.getId() != device1.getId()
    }

    void testShouldFailToCreateDeviceWithEmptyId() {
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = ""

        shouldFail {
            new DeviceManager().createWebDriverDevice(category, deviceId)
        }
    }


    void testShouldFailToCreateDeviceWithNullId() {
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = null

        shouldFail {
            new DeviceManager().createWebDriverDevice(category, deviceId)
        }
    }

    void testShouldFailToCreateDeviceWithNullCategory() {

        shouldFail {
            new DeviceManager().createWebDriverDevice(null)
        }
    }

    void testShouldFailToCreateTwoDevicesWithSameId() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category0 = DeviceCategory.ANDROID
        DeviceCategory category1 = DeviceCategory.IOS
        String deviceId = "phone"

        shouldFail {
            deviceManager.createWebDriverDevice(category0, deviceId)
            deviceManager.createWebDriverDevice(category1, deviceId)
        }
    }

    void testShouldFailCreatingDeviceMissingInCapabilities() {
        System.setProperty(DeviceManager.SYSTEM_PROPERTY_CAPABILITIES, capsWithoutAndroid)

        shouldFail {
            new DeviceManager().createWebDriverDevice(DeviceCategory.ANDROID)
        }
    }

    void testShouldHaveActiveDeviceWithCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID

        deviceManager.createWebDriverDevice(category)

        assert deviceManager.hasActiveDevice(category)
    }

    void testShouldHaveActiveDeviceWithId() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = "phone"

        deviceManager.createWebDriverDevice(category, deviceId)

        assert deviceManager.hasActiveDevice(deviceId)
    }

    void testShouldHaveNoActiveDeviceWithCategory() {
        assert !new DeviceManager().hasActiveDevice(DeviceCategory.ANDROID)
    }

    void testShouldGetAllActiveDevices() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category0 = DeviceCategory.ANDROID
        DeviceCategory category1 = DeviceCategory.IOS
        DeviceCategory category2 = DeviceCategory.BROWSER

        1.upto(10, {
            deviceManager.createWebDriverDevice(category0, it.toString())
        })
        11.upto(20, {
            deviceManager.createWebDriverDevice(category1, it.toString())
        })
        21.upto(30, {
            deviceManager.createWebDriverDevice(category2, it.toString())
        })

        assert deviceManager.getAllActiveDevices().size() == 30
    }


    void testShouldGetAllActiveDevicesWithSelectedCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category0 = DeviceCategory.ANDROID
        DeviceCategory category1 = DeviceCategory.IOS
        DeviceCategory category2 = DeviceCategory.BROWSER

        1.upto(10, {
            deviceManager.createWebDriverDevice(category0, it.toString())
        })
        11.upto(20, {
            deviceManager.createWebDriverDevice(category1, it.toString())
        })
        21.upto(30, {
            deviceManager.createWebDriverDevice(category2, it.toString())
        })

        assert deviceManager.getAllActiveDevices(category0).size() == 10
    }


    void testShouldGetLastlyCreatedActiveDevice() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID

        1.upto(10, {
            deviceManager.createWebDriverDevice(category, it.toString())
        })

        WebDriverDevice device0 = deviceManager.createWebDriverDevice(category)
        assert deviceManager.getActiveDevice().is(device0)
    }

    void testShouldGetFirstActiveDeviceByCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID

        WebDriverDevice device = deviceManager.createWebDriverDevice(category)
        1.upto(10, {
            deviceManager.createWebDriverDevice(category, it.toString())
        })

        assert deviceManager.getActiveDevice(category).is(device)
    }

    void testShouldGetActiveDeviceById() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = "phone"

        WebDriverDevice device = deviceManager.createWebDriverDevice(category, deviceId)
        assert deviceManager.getActiveDevice(deviceId).is(device)
    }


    void testShouldFailToGetActiveDeviceByCategoryIfNoSuchDeviceCreated() {
        DeviceCategory category = DeviceCategory.ANDROID

        shouldFail {
            new DeviceManager().getActiveDevice(category)
        }
    }


    void testShouldFailToGetActiveDeviceByIdIfNoSuchDeviceCreated() {
        String deviceId = "phone"

        shouldFail {
            new DeviceManager().getActiveDevice(deviceId)
        }
    }


    void testShouldQuitDeviceWithSelectedId() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = "phone"

        deviceManager.createWebDriverDevice(category, deviceId)
        deviceManager.quitDevice(deviceId)

        shouldFail {
            deviceManager.getActiveDevice(deviceId)
        }
    }

    void testShouldQuitAllDevices() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID

        1.upto(10, {
            deviceManager.createWebDriverDevice(category, it.toString())
        })
        deviceManager.quitAllDevices()

        assert deviceManager.getAllActiveDevices().size() == 0
    }


    void testShouldQuitAllDevicesWithSelectedCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category0 = DeviceCategory.ANDROID
        DeviceCategory category1 = DeviceCategory.IOS
        DeviceCategory category2 = DeviceCategory.BROWSER

        1.upto(10, {
            deviceManager.createWebDriverDevice(category0, it.toString())
        })
        11.upto(20, {
            deviceManager.createWebDriverDevice(category1, it.toString())
        })
        21.upto(30, {
            deviceManager.createWebDriverDevice(category2, it.toString())
        })
        deviceManager.quitAllDevices(category1)

        assert deviceManager.getAllActiveDevices(category1).size() == 0
    }

    void tearDown() {
        System.clearProperty(DeviceManager.SYSTEM_PROPERTY_CAPABILITIES)
    }
}
