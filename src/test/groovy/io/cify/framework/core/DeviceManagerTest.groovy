package io.cify.framework.core

import groovy.json.JsonSlurper

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

        Device device = new DeviceManager().createDevice(category)
        assert device.getCategory().is(category)
    }


    void testShouldCreateIOSDevice() {
        DeviceCategory category = DeviceCategory.IOS

        Device device = new DeviceManager().createDevice(category)
        assert device.getCategory().is(category)
    }


    void testShouldCreateBrowserDevice() {
        DeviceCategory category = DeviceCategory.BROWSER

        Device device = new DeviceManager().createDevice(category)
        assert device.getCategory().is(category)
    }


    void testShouldCreateDeviceWithId() {
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = "phone"

        Device device = new DeviceManager().createDevice(category, deviceId)
        assert device.getId().is(deviceId)
    }


    void testShouldCreateMultipleUniqueDevicesWithSameCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId0 = "phone-0"
        String deviceId1 = "phone-1"

        Device device0 = deviceManager.createDevice(category, deviceId0)
        Device device1 = deviceManager.createDevice(category, deviceId1)

        assert device0.getId() != device1.getId()
    }

    void testShouldFailToCreateDeviceWithEmptyId() {
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = ""

        shouldFail {
            new DeviceManager().createDevice(category, deviceId)
        }
    }


    void testShouldFailToCreateDeviceWithNullId() {
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = null

        shouldFail {
            new DeviceManager().createDevice(category, deviceId)
        }
    }

    void testShouldFailToCreateDeviceWithNullCategory() {

        shouldFail {
            new DeviceManager().createDevice(null)
        }
    }

    void testShouldFailToCreateTwoDevicesWithSameId() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category0 = DeviceCategory.ANDROID
        DeviceCategory category1 = DeviceCategory.IOS
        String deviceId = "phone"

        shouldFail {
            deviceManager.createDevice(category0, deviceId)
            deviceManager.createDevice(category1, deviceId)
        }
    }

    void testShouldFailCreatingDeviceMissingInCapabilities() {
        System.setProperty(DeviceManager.SYSTEM_PROPERTY_CAPABILITIES, capsWithoutAndroid)

        shouldFail {
            new DeviceManager().createDevice(DeviceCategory.ANDROID)
        }
    }

    void testShouldHaveActiveDeviceWithCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID

        deviceManager.createDevice(category)

        assert deviceManager.hasActiveDevice(category)
    }

    void testShouldHaveActiveDeviceWithId() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = "phone"

        deviceManager.createDevice(category, deviceId)

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
            deviceManager.createDevice(category0, it.toString())
        })
        11.upto(20, {
            deviceManager.createDevice(category1, it.toString())
        })
        21.upto(30, {
            deviceManager.createDevice(category2, it.toString())
        })

        assert deviceManager.getAllActiveDevices().size() == 30
    }


    void testShouldGetAllActiveDevicesWithSelectedCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category0 = DeviceCategory.ANDROID
        DeviceCategory category1 = DeviceCategory.IOS
        DeviceCategory category2 = DeviceCategory.BROWSER

        1.upto(10, {
            deviceManager.createDevice(category0, it.toString())
        })
        11.upto(20, {
            deviceManager.createDevice(category1, it.toString())
        })
        21.upto(30, {
            deviceManager.createDevice(category2, it.toString())
        })

        assert deviceManager.getAllActiveDevices(category0).size() == 10
    }


    void testShouldGetFirstActiveDevice() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID

        Device device0 = deviceManager.createDevice(category)
        1.upto(10, {
            deviceManager.createDevice(category, it.toString())
        })

        assert deviceManager.getActiveDevice().is(device0)
    }

    void testShouldGetFirstActiveDeviceByCategory() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID

        Device device = deviceManager.createDevice(category)
        1.upto(10, {
            deviceManager.createDevice(category, it.toString())
        })

        assert deviceManager.getActiveDevice(category).is(device)
    }

    void testShouldGetActiveDeviceById() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID
        String deviceId = "phone"

        Device device = deviceManager.createDevice(category, deviceId)
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

        deviceManager.createDevice(category, deviceId)
        deviceManager.quitDevice(deviceId)

        shouldFail {
            deviceManager.getActiveDevice(deviceId)
        }
    }

    void testShouldQuitAllDevices() {
        DeviceManager deviceManager = new DeviceManager()
        DeviceCategory category = DeviceCategory.ANDROID

        1.upto(10, {
            deviceManager.createDevice(category, it.toString())
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
            deviceManager.createDevice(category0, it.toString())
        })
        11.upto(20, {
            deviceManager.createDevice(category1, it.toString())
        })
        21.upto(30, {
            deviceManager.createDevice(category2, it.toString())
        })
        deviceManager.quitAllDevices(category1)

        assert deviceManager.getAllActiveDevices(category1).size() == 0
    }

    void testGetConfiguration() {
        assert !DeviceManager.getConfiguration().is(null)
    }

    void tearDown() {
        System.clearProperty(DeviceManager.SYSTEM_PROPERTY_CAPABILITIES)
    }
}
