package io.cify.framework.core.interfaces

import io.cify.framework.core.Capabilities
import io.cify.framework.core.WebDriverDevice
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
     * @return WebDriverDevice
     */
    WebDriverDevice createWebDriverDevice(DeviceCategory category)

    /**
     * Creates custom device with Custom category
     * @return
     */
    ICustomDevice createCustomDevice(ICustomDevice customDevice)

    /**
     * Create Custom Device with specified id
     * @param deviceId
     * @return
     */
    ICustomDevice createCustomDevice()

    /**
     * Creates device with category and given id
     * @param category
     * @param deviceId
     * @return WebDriverDevice
     */
    WebDriverDevice createWebDriverDevice(DeviceCategory category, String deviceId)

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
     * @return WebDriverDevice
     */
    ICustomDevice getActiveDevice()

    /**
     * Gets active device by category
     * @param category
     * @return WebDriverDevice
     */
    ICustomDevice getActiveDevice(DeviceCategory category)

    /**
     * Gets active device
     * @param deviceId
     * @return WebDriverDevice
     */
    ICustomDevice getActiveDevice(String deviceId)

    /**
     * Gets all active devices
     * @return List < WebDriverDevice >
     */
    List<ICustomDevice> getAllActiveDevices()

    /**
     * Gets all active devices by category
     * @param category
     * @return List < WebDriverDevice >
     */
    List<ICustomDevice> getAllActiveDevices(DeviceCategory category)

    /**
     * Closes device with id
     * @param deviceId
     */
    void quitDevice(String deviceId)

    /**
     * Closes device
     * @param device
     */
    void quitDevice(ICustomDevice device)

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