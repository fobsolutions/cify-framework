package io.cify.framework

import io.cify.framework.actions.IActions
import io.cify.framework.core.DeviceManager
import io.cify.framework.core.models.Device

/**
 * This class is responsible for getting actions
 */
public class Actions {

    /**
     * Gets default actions for first active device
     *
     * @return IActions interface object
     * */
    public static IActions getActions() {
        return getActions(DeviceManager.getActiveDevice())
    }

    /**
     * Gets default actions for device with id
     *
     * @param acticeDeviceId - Device id for active device
     * @return IActions interface object
     * */
    public static IActions getActions(String activeDeviceId) {
        return getActions(DeviceManager.getActiveDevice(activeDeviceId))
    }

    /**
     * Gets default actions for device
     *
     * @param device
     * @return IActions interface object
     * */
    public static IActions getActions(Device device) {
        return getCustomActionsByDevice(device, "io.cify.framework.actions.Actions")
    }

    /**
     * Gets custom actions for first active device from classpath
     *
     * @param class package and name
     * @return IActions interface object
     * */
    public static IActions getCustomActions(String className) {
        return getCustomActions(DeviceManager.getActiveDevice(), className)
    }

    /**
     * Gets custom actions for active device with id and className
     *
     * @param activeDeviceId - Id from actice device
     * @param className - class package and name
     * @return IActions interface object
     * */
    public static IActions getCustomActions(String activeDeviceId, String className) {
        return getCustomActions(DeviceManager.getActiveDevice(activeDeviceId), className)
    }

    /**
     * Gets custom actions for device from classpath
     *
     * @param device - Device object
     * @param className - class package and name
     * @return IActions interface object
     * */
    public static IActions getCustomActions(Device device, String className) {
        return getCustomActionsByDevice(device, className)
    }

    /**
     * Gets custom actions for device from classpath
     *
     * @param device - Device object
     * @param className - class package and name
     * @return IActions interface object
     * */
    private static IActions getCustomActionsByDevice(Device device, String className) {
        return (IActions) Factory.get(device, className)
    }


}
