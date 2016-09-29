package io.cify.framework.core.interfaces

import io.cify.framework.core.Capabilities
import io.cify.framework.core.Device
import io.cify.framework.core.DeviceCategory

/**
 * Created by FOB Solutions
 */
interface IDeviceManager {

    Capabilities getCapabilities()

    Device createDevice(DeviceCategory category)

    Device createDevice(DeviceCategory category, String deviceId)

    boolean hasActiveDevice(DeviceCategory category)

    boolean hasActiveDevice(String deviceId)

    Device getActiveDevice()

    Device getActiveDevice(DeviceCategory category)

    Device getActiveDevice(String deviceId)

    List<Device> getAllActiveDevices()

    List<Device> getAllActiveDevices(DeviceCategory category)

    void quitDevice(String deviceId)

    void quitDevice(Device device)

    void quitAllDevices()

    void quitAllDevices(DeviceCategory category)

}