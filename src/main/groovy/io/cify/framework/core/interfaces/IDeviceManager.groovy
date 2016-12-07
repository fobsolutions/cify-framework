package io.cify.framework.core.interfaces

import io.cify.framework.core.Capabilities
import io.cify.framework.core.Device
import io.cify.framework.core.DeviceCategory

/**
 * Created by FOB Solutions
 */
interface IDeviceManager {

    /**
     * Gets capabilities for current run
     * @return Capabilities
     */
    Capabilities getCapabilities()

    /**
     * Creates device with category
     * @param category
     * @return Device
     */
    Device createDevice(DeviceCategory category)

    /**
     * Creates device with category and given id
     * @param category
     * @param deviceId
     * @return Device
     */
    Device createDevice(DeviceCategory category, String deviceId)

    /**
     * Checks if device already active
     * @param category
     * @return boolean
     */
    boolean hasActiveDevice(DeviceCategory category)

    /**
     * Checks if device is already active
     * @param deviceId
     * @return boolean
     */
    boolean hasActiveDevice(String deviceId)

    /**
     * Gets active device
     * @return Device
     */
    Device getActiveDevice()

    /**
     * Gets active device by category
     * @param category
     * @return Device
     */
    Device getActiveDevice(DeviceCategory category)

    /**
     * Gets active device
     * @param deviceId
     * @return Device
     */
    Device getActiveDevice(String deviceId)

    /**
     * Gets all active devices
     * @return List < Device >
     */
    List<Device> getAllActiveDevices()

    /**
     * Gets all active devices by category
     * @param category
     * @return List < Device >
     */
    List<Device> getAllActiveDevices(DeviceCategory category)

    /**
     * Closes device with id
     * @param deviceId
     */
    void quitDevice(String deviceId)

    /**
     * Closes device
     * @param device
     */
    void quitDevice(Device device)

    /**
     * Closes all devices
     */
    void quitAllDevices()

    /**
     * Closes all devices by category
     * @param category
     */
    void quitAllDevices(DeviceCategory category)

}