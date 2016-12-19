package io.cify.framework.core

import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils
import groovy.json.internal.LazyMap
import io.cify.framework.core.interfaces.IDeviceManager
import io.cify.framework.reporting.TestReportManager
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

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('DEVICE MANAGER') as Marker

    /**
     * System property to be used to pass capabilities to Device Manager
     * */
    public static final String SYSTEM_PROPERTY_CAPABILITIES = "capabilities"

    /**
     * System property to be used to pass credentials of device farm providers to Device Manager
     */
    public static final String SYSTEM_PROPERTY_CREDENTIALS = "credentials"

    private Capabilities capabilities
    private List<Device> devices = []
    private static volatile DeviceManager instance
    private LazyMap credentials

    /**
     * Default constructor for Device Manager
     * */
    public DeviceManager() {
        LOG.debug(MARKER, 'Create new DeviceManager')
        try {
            Configuration.setupFrameworkConfiguration()

            String capabilitiesJson = StringEscapeUtils.unescapeJava(System.getProperty(SYSTEM_PROPERTY_CAPABILITIES))
            this.capabilities = Capabilities.parseFromJsonString(capabilitiesJson)

            String credentialsRaw = System.getProperty(SYSTEM_PROPERTY_CREDENTIALS, "{}")
            this.credentials = new JsonSlurper().parseText(credentialsRaw) as LazyMap
        } catch (all) {
            LOG.debug(MARKER, all.message, all)
            throw new CifyFrameworkException("Failed to create Device Manager instance: $all.message")
        }
    }

    /**
     * Returns instance of Device Manager
     *
     * @return DeviceManager instance
     */
    public static DeviceManager getInstance() {
        LOG.debug(MARKER, 'Get instance of DeviceManager')
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
        LOG.debug(MARKER, 'Get DeviceManager capabilities')
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
        LOG.debug(MARKER, "Create new device with category $category and device id $deviceId")
        try {

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

            credentials.each { key, value ->
                desiredCapabilities.setCapability(key, value)
            }

            Device device = new Device(deviceId, category, desiredCapabilities)
            devices.add(device)

        } catch (all) {
            LOG.debug(MARKER, all.message, all)
            throw new CifyFrameworkException("Failed to create device cause $all.message")
        }

        addDeviceToTestReport(deviceId,category.toString())
        return getActiveDevice(deviceId)
    }

    /**
     * Add device info to test scenario report
     *
     * @param String device id
     *
     * @param String device category
     */
    private static void addDeviceToTestReport(String deviceId, String category){
        TestReportManager.addDeviceToTestReport(deviceId,category)
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
        LOG.debug(MARKER, "Check if device with category $category exists")
        Device device = devices.find { device ->
            device.getCategory() == category
        }

        if (device == null) {
            LOG.debug(MARKER, "No device with category $category found")
            return false
        }

        LOG.debug(MARKER, "Device with category $category found")
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
        LOG.debug(MARKER, "Check if device with id $deviceId exists")
        Device device = devices.find { device ->
            device.getId() == deviceId
        }

        if (device == null) {
            LOG.debug(MARKER, "No device with id $deviceId found")
            return false
        }

        LOG.debug(MARKER, "Device with id $deviceId found")
        return true
    }

    /**
     * Returns all active devices
     *
     * @return List < Device >
     */
    @Override
    List<Device> getAllActiveDevices() {
        LOG.debug(MARKER, "Get all active devices")
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
        LOG.debug(MARKER, "Find all active devices of category $category")
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
        LOG.debug(MARKER, "Get first active device")
        try {
            return devices.first()
        } catch (all) {
            LOG.debug(MARKER, all.message, all)
            throw new CifyFrameworkException("No active device found")
        }
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
        LOG.debug(MARKER, "Find first active devices of category $category")
        Device device = devices.find { device ->
            device.getCategory() == category
        }
        if (device == null) {
            LOG.debug(MARKER, "No active device with category $category found")
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
        LOG.debug(MARKER, "Find active device with id $deviceId")
        Device device = devices.find { device ->
            device.getId() == deviceId
        }
        if (device == null) {
            LOG.debug(MARKER, "No active device with id $deviceId found")
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
        LOG.debug(MARKER, "Quit device with id $deviceId")
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
        LOG.debug(MARKER, "Quit device $device")
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
        LOG.debug(MARKER, "Quit all active devices")
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
        LOG.debug(MARKER, "Quit all active devices of selected category")
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