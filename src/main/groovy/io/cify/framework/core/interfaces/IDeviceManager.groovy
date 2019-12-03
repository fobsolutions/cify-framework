package io.cify.framework.core.interfaces

import io.cify.common.DeviceCategory
import io.cify.common.capability.conf.CapabilityConfig
import io.cify.framework.core.Device

/**
 * Created by FOB Solutions
 */
interface IDeviceManager {

    /**
     * Gets capabilities for current run
     * @return Capabilities
     */
    CapabilityConfig getCapabilities()

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
     * Creates device with capabilities identified by capabilityId
     * @param capabilityId capability identifier
     * @return Device
     */
    Device createDevice(String capabilityId)

    /**
     * Creates device with capabilities identified by capabilityId and with unique deviceId
     * @param capabilityId capability identifier
     * @param deviceId unique device id
     * @return Device
     */
    Device createDevice(String capabilityId, String deviceId)

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
     * Sets device with device id as active
     * @param deviceId
     */
    void setDeviceWithDeviceIdActive(String deviceId)

    /**
     * Sets device created using capabilities identified with given capabilityId as active
     * @param capabilityId
     */
    void setDeviceWithCapabilityIdActive(String capabilityId)

    /**
     * Sets first device from device list with given category as active
     * @param category DeviceCategory
     */
    void setFirstDeviceOfCategoryActive(DeviceCategory category)

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