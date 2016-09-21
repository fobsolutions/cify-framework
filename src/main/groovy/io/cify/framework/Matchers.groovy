package io.cify.framework

import io.cify.framework.core.DeviceManager
import io.cify.framework.core.models.Device
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
        return getMatchers(DeviceManager.getActiveDevice())
    }

    /**
     * Gets matchers for active device with id
     *
     * @param activeDeviceId - Actice device id
     * @return IMatchers interface
     * */
    public static IMatchers getMatchers(String activeDeviceId) {
        return getMatchers(DeviceManager.getActiveDevice(activeDeviceId))
    }

    /**
     * Gets default matchers for device
     *
     * @param device - Device object
     * @return IMatchers interface
     * */
    public static IMatchers getMatchers(Device device) {
        return getCustomMatchersByDevice(device, "io.cify.framework.matchers.Matchers")
    }

    /**
     * Gets custom matchers for first active device from classpath
     *
     * @param className - package and class name
     * @return IMatchers interface
     * */
    public static IMatchers getCustomMatchers(String className) {
        return getCustomMatchers(DeviceManager.getActiveDevice(), className)
    }

    /**
     * Gets custom matchers for device with id from classpath
     *
     * @param acticeDeviceId - Id from active device
     * @param className - package and class name
     * @return IMatchers interface
     * */
    public static IMatchers getCustomMatchers(String activeDeviceId, String className) {
        return getCustomMatchers(DeviceManager.getActiveDevice(activeDeviceId), className)
    }

    /**
     * Gets custom matchers for device from classpath
     *
     * @param className - package and class name
     * @param device - Device object
     * @return IMatchers interface
     * */
    public static IMatchers getCustomMatchers(Device device, String className) {
        return getCustomMatchersByDevice(device, className)
    }

    /**
     * Gets custom matchers for device from classpath
     *
     * @param className - package and class name
     * @param device - Device object
     * @return IMatchers interface
     * */
    private static IMatchers getCustomMatchersByDevice(Device device, String className) {
        return (IMatchers) Factory.get(device, className)
    }

}
