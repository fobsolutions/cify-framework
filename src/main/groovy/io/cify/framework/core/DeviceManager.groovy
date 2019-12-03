package io.cify.framework.core

import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils
import groovy.json.internal.LazyMap
import io.cify.common.DeviceCategory
import io.cify.common.capability.conf.CapabilityConfig
import io.cify.common.capability.parse.CapabilityParser
import io.cify.framework.core.interfaces.IDeviceManager
import io.cify.framework.reporting.TestReportManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.openqa.selenium.remote.DesiredCapabilities

import static io.cify.common.CifyConstants.SYSTEM_PROPERTY_CAPABILITIES
import static io.cify.common.CifyConstants.SYSTEM_PROPERTY_CREDENTIALS
import static io.cify.common.capability.conf.ICapabilityConfig.PARAM_AVAILABLE
import static java.util.UUID.randomUUID

/**
 * Created by FOB Solutions
 *
 * This class is responsible for managing Devices
 */

class DeviceManager implements IDeviceManager {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('DEVICE MANAGER') as Marker
    private static final String CAPABILITY_ID = "capabilityId"

    private CapabilityConfig capabilities
    private List<Device> devices = []
    private static volatile DeviceManager instance
    private LazyMap credentials

    /**
     * Default constructor for Device Manager
     * */
    private DeviceManager() {
//        LOG.debug(MARKER, 'Create new DeviceManager')
        try {
            Configuration.setupFrameworkConfiguration()

            String capabilitiesJson = StringEscapeUtils.unescapeJava(System.getProperty(SYSTEM_PROPERTY_CAPABILITIES))
            this.capabilities = new CapabilityParser().parse(capabilitiesJson, "", CapabilityParser.STRATEGY_ALL_IN_ONE)[0]

            String credentialsRaw = System.getProperty(SYSTEM_PROPERTY_CREDENTIALS, "{}")
            this.credentials = new JsonSlurper().parseText(credentialsRaw) as LazyMap

        } catch (all) {
//            LOG.debug(MARKER, all.message, all)
            throw new CifyFrameworkException("Failed to create Device Manager instance: $all.message")
        }
    }

    /**
     * Returns instance of Device Manager
     *
     * @return DeviceManager instance
     */
    static DeviceManager getInstance() {
//        LOG.debug(MARKER, 'Get instance of DeviceManager')
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
    CapabilityConfig getCapabilities() {
//        LOG.debug(MARKER, 'Get DeviceManager capabilities')
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
        DesiredCapabilities desiredCapabilities
//        LOG.debug(MARKER, "Create new device with category $category and device id $deviceId")
        try {

            if (deviceId == null || deviceId.isEmpty()) {
                throw new CifyFrameworkException("Id is null or empty")
            }
            if (hasActiveDevice(deviceId)) {
                throw new CifyFrameworkException("Device with id $deviceId already exists")
            }

            desiredCapabilities = capabilities.getDesiredCapabilities(category)

            if (desiredCapabilities.asMap().isEmpty()) {
                throw new CifyFrameworkException("No capabilities provided for $category")
            }

            credentials.each { key, value ->
                desiredCapabilities.setCapability(key, value)
            }

            deviceId = deviceId.replace("_", "-")
            Device device = new Device(deviceId, category, desiredCapabilities)
            devices.add(device)
            setDeviceActive(device)
        } catch (all) {
//            LOG.debug(MARKER, all.message, all)
            throw new CifyFrameworkException("Failed to create device. Cause: $all.message")
        }

        addDeviceToTestReport(deviceId, category.toString())
        setAvailable(desiredCapabilities.getCapability(CAPABILITY_ID) as String, false)

        return getActiveDevice(deviceId)
    }

    /**
     * Creates device with capabilities identified by capabilityId
     * @param capabilityId capability identifier
     * @return Device
     */
    @Override
    Device createDevice(String capabilityId) {
        createDevice(capabilityId, generateRandomDeviceId())
    }

    /**
     * Creates device with capabilities identified by capabilityId and with unique deviceId
     * @param capabilityId capability identifier
     * @param deviceId unique device id
     * @return Device
     */
    @Override
    Device createDevice(String capabilityId, String deviceId) {
        DeviceCategory category
        DesiredCapabilities desiredCapabilities
//        LOG.debug(MARKER, "Create new device with capabilityId $capabilityId")
        try {

            if (capabilityId == null || capabilityId.isEmpty()) {
                throw new CifyFrameworkException("CapabilityId is null or empty")
            }

            if (deviceId == null || deviceId.isEmpty()) {
                throw new CifyFrameworkException("DeviceId is null or empty")
            }

            if (hasActiveDevice(deviceId)) {
                throw new CifyFrameworkException("Device with id $deviceId already exists")
            }

            desiredCapabilities = capabilities.getDesiredCapabilities(capabilityId)

            if (desiredCapabilities.asMap().isEmpty()) {
                throw new CifyFrameworkException("No capabilities provided for $capabilityId")
            }

            credentials.each { key, value ->
                desiredCapabilities.setCapability(key, value)
            }

            category = capabilities.getCategory(capabilityId)
            Device device = new Device(deviceId, category, desiredCapabilities)
            devices.add(device)
            setDeviceActive(device)
        } catch (all) {
//            LOG.debug(MARKER, all.message, all)
            throw new CifyFrameworkException("Failed to create device. Cause: $all.message")
        }

        addDeviceToTestReport(deviceId, category.toString())
        setAvailable(desiredCapabilities.getCapability(CAPABILITY_ID) as String, false)

        return getActiveDevice(deviceId)
    }

    /**
     * Add device info to test scenario report
     *
     * @param String device id
     *
     * @param String device category
     */
    private static void addDeviceToTestReport(String deviceId, String category) {
        TestReportManager.addDeviceToTestReport(deviceId, category)
    }

    /**
     * Checks if a device of selected category exists
     *
     * @param category device category
     *
     * @return boolean
     */
    @Override
    boolean hasActiveDevice(DeviceCategory category) {
//        LOG.debug(MARKER, "Check if device with category $category exists")
        Device device = devices.find { device ->
            device.getCategory() == category
        }

        if (device == null) {
//            LOG.debug(MARKER, "No device with category $category found")
            return false
        }

//        LOG.debug(MARKER, "Device with category $category found")
        return true
    }

    /**
     * Checks if a device with id exists
     *
     * @param deviceId device id
     *
     * @return boolean
     */
    @Override
    boolean hasActiveDevice(String deviceId) {
//        LOG.debug(MARKER, "Check if device with id $deviceId exists")
        Device device = devices.find { device ->
            device.getId() == deviceId
        }

        if (device == null) {
//            LOG.debug(MARKER, "No device with id $deviceId found")
            return false
        }

//        LOG.debug(MARKER, "Device with id $deviceId found")
        return true
    }

    /**
     * Returns all devices
     *
     * @return List < Device >
     */
    @Override
    List<Device> getAllActiveDevices() {
//        LOG.debug(MARKER, "Get all active devices")
        return devices
    }

    /**
     * Returns list of all devices by category
     *
     * @param category device category
     *
     * @return List < Device >
     */
    @Override
    List<Device> getAllActiveDevices(DeviceCategory category) {
//        LOG.debug(MARKER, "Find all active devices of category $category")
        return devices.findAll { device ->
            device.getCategory() == category
        }
    }

    /**
     * Returns currently active device
     *
     * @return Device
     */
    @Override
    Device getActiveDevice() {
//        LOG.debug(MARKER, "Get first active device")
        Device device = devices.find { it.active }
        if (!device) {
//            LOG.debug(MARKER, "No active device found")
            throw new CifyFrameworkException("No active device found")
        }
        return device
    }

    /**
     * Returns first device by category
     *
     * @param category device category
     *
     * @return Device
     * @throws CifyFrameworkException  if no device found
     */
    @Override
    Device getActiveDevice(DeviceCategory category) {
//        LOG.debug(MARKER, "Find first active devices of category $category")
        Device device = devices.find { device ->
            device.getCategory() == category
        }
        if (device == null) {
//            LOG.debug(MARKER, "No active device with category $category found")
            throw new CifyFrameworkException("No active device with category $category found")
        }
        return device
    }

    /**
     * Returns device by id
     *
     * @param deviceId device unique id
     *
     * @return Device
     * @throws CifyFrameworkException  if no device found
     */
    @Override
    Device getActiveDevice(String deviceId) {
//        LOG.debug(MARKER, "Find active device with id $deviceId")
        Device device = devices.find { device ->
            device.getId() == deviceId
        }
        if (device == null) {
//            LOG.debug(MARKER, "No active device with id $deviceId found")
            throw new CifyFrameworkException("No active device with id $deviceId found")
        }
        return device
    }

    /**
     * Sets device active
     *
     * @param device Device to set active
     */
    void setDeviceActive(Device device) {
        try {
            device.active = true
            getAllActiveDevices().each {
                if (it != device) {
                    it.active = false
                }
            }
        } catch (NullPointerException ignored) {
            throw new CifyFrameworkException("Could not set device as active. Requested device does not exists")
        }
    }

    /**
     * Sets device with device id as active
     * @param deviceId
     */
    @Override
    void setDeviceWithDeviceIdActive(String deviceId) {
        Device device = getAllActiveDevices().find { it.getId() == deviceId }
        setDeviceActive(device)
    }

    /**
     * Sets device created using capabilities identified with given capabilityId as active
     * @param capabilityId
     */
    @Override
    void setDeviceWithCapabilityIdActive(String capabilityId) {
        Device device = getAllActiveDevices().find { it.getCapabilities().getCapability(CAPABILITY_ID) == capabilityId }
        setDeviceActive(device)
    }

    /**
     * Sets first device from device list with given category as active
     *
     * @param category DeviceCategory
     */
    @Override
    void setFirstDeviceOfCategoryActive(DeviceCategory category) {
        Device device = getActiveDevice(category)
        device.active = true
        getAllActiveDevices().each {
            if (it != device) {
                it.active = false
            }
        }
    }

    /**
     * Quits device
     *
     * @param deviceId device id
     */
    @Override
    void quitDevice(String deviceId) {
//        LOG.debug(MARKER, "Quit device with id $deviceId")
        Device device = getActiveDevice(deviceId)
        setAvailable(device)
        quitDevice(device)
    }

    /**
     * Quits device
     *
     * @param device device
     */
    @Override
    void quitDevice(Device device) {
//        LOG.debug(MARKER, "Quit device $device")
        if (device != null) {
            device.quit()
            setAvailable(device)
            devices.removeElement(device)
        }
    }

    /**
     * Quits all active devices
     *
     */
    @Override
    void quitAllDevices() {
//        LOG.debug(MARKER, "Quit all active devices")
        devices.each { device ->
            device.quit()
            setAvailable(device)
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
//        LOG.debug(MARKER, "Quit all active devices of selected category")
        getAllActiveDevices(category).each { device ->
            quitDevice(device)
        }
    }

    /**
     * Creates random device uuid
     *
     * @return String
     */
    private static String generateRandomDeviceId() {
        def uuid = randomUUID() as String
        return uuid.toUpperCase()
    }

    /**
     * Marks capability used by given device as available
     * That device should be removed after calling this method
     * @param device
     */
    private void setAvailable(Device device) {
        setAvailable(device.getCapabilities().getCapability(CAPABILITY_ID) as String, true)
    }

    private void setAvailable(String capabilityId, isAvailable) {
        capabilities.setCapability(capabilityId, PARAM_AVAILABLE, isAvailable)
    }
}