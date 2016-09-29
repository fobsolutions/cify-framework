package io.cify.framework

import io.cify.framework.core.Device
import io.cify.framework.core.DeviceManager
import io.cify.framework.matchers.IMatchers

/**
 * This class is responsible for getting matchers
 * */
public class Matchers {

    /**
     * Gets default matchers for first active device
     *
     * @return IMatchers interface
     * */
    public static IMatchers getMatchers() {
        return getMatchers(DeviceManager.getInstance().getActiveDevice())
    }

    /**
     * Gets matchers for active device with id
     *
     * @param activeDeviceId - Actice device id
     * @return IMatchers interface
     * */
    public static IMatchers getMatchers(String activeDeviceId) {
        return getMatchers(DeviceManager.getInstance().getActiveDevice(activeDeviceId))
    }

    /**
     * Gets default matchers for device
     *
     * @param device - Device object
     * @return IMatchers interface
     * */
    public static IMatchers getMatchers(Device device) {
        return (IMatchers) Factory.get(device, "io.cify.framework.matchers.Matchers")
    }

    /**
     * Gets custom matchers class for first active device and given classpath
     *
     * @param className - package and class name
     * @return generic class object
     * */
    public static def getCustomMatchers(String className) {
        return getCustomMatchers(DeviceManager.getInstance().getActiveDevice(), className)
    }

    /**
     * Gets custom matchers class for device with id and given classpath
     *
     * @param acticeDeviceId - Id from active device
     * @param className - package and class name
     * @return generic class object
     * */
    public static def getCustomMatchers(String activeDeviceId, String className) {
        return getCustomMatchers(DeviceManager.getInstance().getActiveDevice(activeDeviceId), className)
    }

    /**
     * Gets custom matchers class for device and given classpath
     *
     * @param className - package and class name
     * @param device - Device object
     * @return generic class object
     * */
    public static def getCustomMatchers(Device device, String className) {
        return Factory.get(device, className)
    }
}
