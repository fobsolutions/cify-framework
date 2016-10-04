package io.cify.framework

import io.cify.framework.core.Device
import io.cify.framework.core.DeviceCategory
import io.cify.framework.factory.FactoryTestClassInterface
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * Created by FOB Solutions
 */
class FactoryTest extends GroovyTestCase {

    private Device device
    private Device deviceAndroidUIType
    private Device deviceIOSUIType

    private String className = 'io.cify.framework.factory.FactoryTestClass'
    private String classNameBad = 'FactoryTestClass'


    void setUp() {
        device = new Device('phone', DeviceCategory.ANDROID, new DesiredCapabilities([:]))
        deviceAndroidUIType = new Device('phone', DeviceCategory.ANDROID, new DesiredCapabilities(['UIType': 'Android']))
        deviceIOSUIType = new Device('phone', DeviceCategory.ANDROID, new DesiredCapabilities(['UIType': 'IOS']))
    }

    void testShouldCreateProxyInstanceForDeviceWithoutUIType() {
        assert Factory.get(device, className) instanceof FactoryTestClassInterface
    }

    void testShouldCreateProxyInstanceForDeviceWithUIType() {
        assert Factory.get(deviceAndroidUIType, className) instanceof FactoryTestClassInterface
    }

    void testShouldFailToCreateProxyInstanceIfImplementationMissingForUIType() {
        shouldFail {
            Factory.get(deviceIOSUIType, className)
        }
    }

    void testShouldFailToCreateProxyInstanceForMissingClass() {
        shouldFail {
            Factory.get(device, classNameBad)
        }
    }

    void testShouldInvokeMethod() {
        FactoryTestClassInterface obj = (FactoryTestClassInterface) Factory.get(device, className)
        assert obj.getTrue(true)
    }
}



