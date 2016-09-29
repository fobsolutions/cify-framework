package io.cify.framework

import io.cify.framework.actions.IActions
import io.cify.framework.core.Device
import io.cify.framework.core.DeviceManager

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
        return getActions(DeviceManager.getInstance().getActiveDevice())
    }

    /**
     * Gets default actions for device with id
     *
     * @param acticeDeviceId - Device id for active device
     * @return IActions interface object
     * */
    public static IActions getActions(String activeDeviceId) {
        return getActions(DeviceManager.getInstance().getActiveDevice(activeDeviceId))
    }

    /**
     * Gets default actions for device
     *
     * @param device
     * @return IActions interface object
     * */
    public static IActions getActions(Device device) {
        return (IActions) Factory.get(device, "io.cify.framework.actions.Actions")
    }

    /**
     * Gets custom actions for first active device and for given classpath
     *
     * @param class package and name
     * @return generic class object
     * */
    public static def getCustomActions(String className) {
        return getCustomActions(DeviceManager.getInstance().getActiveDevice(), className)
    }

    /**
     * Gets custom actions for active device with id and and for given classpath
     *
     * @param activeDeviceId - Id from actice device
     * @param className - class package and name
     * @return generic class object
     * */
    public static def getCustomActions(String activeDeviceId, String className) {
        return getCustomActions(DeviceManager.getInstance().getActiveDevice(activeDeviceId), className)
    }

    /**
     * Gets custom actions for device and for given classpath
     *
     * @param device - Device object
     * @param className - class package and name
     * @return generic class object
     * */
    public static def getCustomActions(Device device, String className) {
        return Factory.get(device, className)
    }
}