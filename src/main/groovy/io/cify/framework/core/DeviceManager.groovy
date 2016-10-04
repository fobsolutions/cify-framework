package io.cify.framework.core

import io.cify.framework.core.interfaces.IDeviceManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.openqa.selenium.remote.DesiredCapabilities

import static java.util.UUID.randomUUID

/**
 * Created by FOB Solutions
 *
 * This class is responsible for managing Devices
 */

class DeviceManager implements IDeviceManager {

    private static final Logger log = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('DEVICE MANAGER') as Marker

    /**
     * System property to be used to pass capabilities to Device Manager
     * */
    public static final String SYSTEM_PROPERTY_CAPABILITIES = "capabilities"

    /**
     * Json string of supported capabilities
     * */
    public static final String SUPPORTED_CAPABILITIES = '''
        {
            "browser" : {
                "capability" : "chrome",
            },
            "android" : {
                "capability" : "android",
                "version" : ""
            },
            "ios" : {
                "capability" : "iphone",
                "version" : ""
            }
        }
    '''

    private Capabilities capabilities
    private List<Device> devices = []
    private static volatile DeviceManager instance

    /**
     * Default constructor for Device Manager
     * */
    public DeviceManager() {
        log.debug(MARKER, 'Create new DeviceManager')
        try {
            String capabilitiesJson = System.getProperty(SYSTEM_PROPERTY_CAPABILITIES, SUPPORTED_CAPABILITIES)
            this.capabilities = Capabilities.parseFromJsonString(capabilitiesJson)
        } catch (all) {
            throw new CifyFrameworkException("Failed to create Device Manager instance: $all.message", all)
        }
    }

    /**
     * Returns instance of Device Manager
     *
     * @return DeviceManager instance
     */
    public static DeviceManager getInstance() {
        log.debug(MARKER, 'Get instance of DeviceManager')
        if (instance == null) {
            synchronized (DeviceManager.class) {
                if (instance == null) {
                    instance = new DeviceManager()
                }
            }
        }
        return instance
    }

    /**
     * Returns Device Manager capabilities
     *
     * @return Capabilities
     */
    @Override
    Capabilities getCapabilities() {
        log.debug(MARKER, 'Get DeviceManager capabilities')
        return capabilities
    }

    /**
     * Creates device of selected category
     *
     * @param category device category
     *
     * @return Device
     */
    @Override
    Device createDevice(DeviceCategory category) {
        return createDevice(category, generateRandomDeviceId())
    }

    /**
     * Creates device of selected category and with unique id
     *
     * @param category device category
     * @param deviceId unique device id
     *
     * @return Device
     * @throws CifyFrameworkException  if device id is null or empty
     * @throws CifyFrameworkException  if active device with same id already exists
     */
    @Override
    Device createDevice(DeviceCategory category, String deviceId) {
        log.debug(MARKER, "Create new device with category $category and device id $deviceId")
        if (deviceId == null || deviceId.isEmpty()) {
            throw new CifyFrameworkException("Failed to create device. Id is null or empty")
        }
        if (hasActiveDevice(deviceId)) {
            throw new CifyFrameworkException("Failed to create device. Device with id $deviceId already exists")
        }

        DesiredCapabilities desiredCapabilities = capabilities.toDesiredCapabilities(category)
        if (desiredCapabilities.asMap().isEmpty()) {
            throw new CifyFrameworkException("Failed to create device. No capabilities provided for $category")
        }

        Device device = new Device(deviceId, category, desiredCapabilities)
        devices.add(device)

        return getActiveDevice(deviceId)
    }

    /**
     * Checks if an active device of selected category exists
     *
     * @param category device category
     *
     * @return boolean
     */
    @Override
    boolean hasActiveDevice(DeviceCategory category) {
        log.debug(MARKER, "Check if device with category $category exists")
        Device device = devices.find { device ->
            device.getCategory() == category
        }

        if (device == null) {
            log.debug(MARKER, "No device with category $category found")
            return false
        }

        log.debug(MARKER, "Device with category $category found")
        return true
    }

    /**
     * Checks if an active device with id exists
     *
     * @param deviceId device id
     *
     * @return boolean
     */
    @Override
    boolean hasActiveDevice(String deviceId) {
        log.debug(MARKER, "Check if device with id $deviceId exists")
        Device device = devices.find { device ->
            device.getId() == deviceId
        }

        if (device == null) {
            log.debug(MARKER, "No device with id $deviceId found")
            return false
        }

        log.debug(MARKER, "Device with id $deviceId found")
        return true
    }

    /**
     * Returns all active devices
     *
     * @return List < Device >
     */
    @Override
    List<Device> getAllActiveDevices() {
        log.debug(MARKER, "Get all active devices")
        return devices
    }

    /**
     * Returns list of all active devices by category
     *
     * @param category device category
     *
     * @return List < Device >
     */
    @Override
    List<Device> getAllActiveDevices(DeviceCategory category) {
        log.debug(MARKER, "Find all active devices of category $category")
        return devices.findAll { device ->
            device.getCategory() == category
        }
    }


    /**
     * Returns first active device
     *
     * @return Device
     */
    @Override
    Device getActiveDevice() {
        log.debug(MARKER, "Get first active device")
        return devices.first()
    }

    /**
     * Returns first active device by category
     *
     * @param category device category
     *
     * @return Device
     * @throws CifyFrameworkException  if no active device found
     */
    @Override
    Device getActiveDevice(DeviceCategory category) {
        log.debug(MARKER, "Find first active devices of category $category")
        Device device = devices.find { device ->
            device.getCategory() == category
        }

        if (device == null) {
            throw new CifyFrameworkException("No active device with category $category found")
        }

        return device
    }

    /**
     * Returns first active device by id
     *
     * @param deviceId device unique id
     *
     * @return Device
     * @throws CifyFrameworkException  if no active device found
     */
    @Override
    Device getActiveDevice(String deviceId) {
        log.debug(MARKER, "Find active device with id $deviceId")
        Device device = devices.find { device ->
            device.getId() == deviceId
        }

        if (device == null) {
            throw new CifyFrameworkException("No active device with id $deviceId found")
        }

        return device
    }


    /**
     * Quits device
     *
     * @param deviceId device id
     */
    @Override
    void quitDevice(String deviceId) {
        log.debug(MARKER, "Quit device with id $deviceId")
        Device device = getActiveDevice(deviceId)
        quitDevice(device)
    }

    /**
     * Quits device
     *
     * @param device device
     */
    @Override
    void quitDevice(Device device) {
        log.debug(MARKER, "Quit device $device")
        if (device != null) {
            device.quit()
            devices.removeElement(device)
        }
    }

    /**
     * Quits all active devices
     *
     */
    @Override
    void quitAllDevices() {
        log.debug(MARKER, "Quit all active devices")
        devices.each { device ->
            device.quit()
        }
        devices.clear()
    }

    /**
     * Quits devices of selected category
     *
     * @param category device category
     */
    @Override
    void quitAllDevices(DeviceCategory category) {
        log.debug(MARKER, "Quit all active devices of selected category")
        getAllActiveDevices(category).each { device ->
            quitDevice(device)
        }
    }

    /**
     * Create random device uuid
     *
     * @return String
     */
    private static String generateRandomDeviceId() {
        def uuid = randomUUID() as String
        return uuid.toUpperCase()
    }
}