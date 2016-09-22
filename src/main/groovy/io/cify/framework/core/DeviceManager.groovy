package io.cify.framework.core

import groovy.json.JsonSlurper
import io.cify.framework.core.models.Device
import io.cify.framework.Constants
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.codehaus.groovy.GroovyException

import static java.util.UUID.randomUUID

/**
 * Created by FOB Solutions
 *
 * This class is responsible for managing Devices
 */

class DeviceManager {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger

    private static final String MAIN_CAPABILITY = "mainCapability"
    private List capabilities
    private Map<String, Device> devices = [:]
    private static volatile DeviceManager instance

    /**
     * Default constructor for DeviceManager
     * */
    private DeviceManager() {
        String capabilitiesString
        LOG.debug("Reading full device list from saved file")
        try {
            String capabilitiesFilePath = System.getProperty("capabilitiesFilePath") != null ? System.getProperty("capabilitiesFilePath") : Constants.PARAMETERS_PATH + Constants.FULL_CAPABILITIES_FILE;
            capabilitiesString = new File(capabilitiesFilePath).text
            LOG.debug("Full capabilities file content: " + capabilitiesString)
            capabilities = new JsonSlurper().parseText(capabilitiesString).capabilities as List
        } catch (all) {
            String error = "Cannot read device list from saved parameters file. Run gradle parameters task to setup parameters file"
            LOG.error(error, all.message, all)
            throw all
        }
    }

    /**
     * returns instance of Device manager
     *
     * @return DeviceManager instance
     */
    private static DeviceManager getInstance() {
        if (instance == null) {
            synchronized (DeviceManager.class) {
                if (instance == null) {
                    instance = new DeviceManager();
                }
            }
        }
        return instance;
    }

    /**
     * Reads main capability from system property
     *
     * @return Map string:string - mainCapability
     */
    private static Map<String, String> readMainCapability() {
        LOG.debug("Reading main capability from system property.")
        String mainCapability = System.getProperty(MAIN_CAPABILITY)
        if (mainCapability == null) {
            throw new GroovyException("main capability not found in system property")
        }
        List mainCapsParams = mainCapability.split(":")
        Map<String, String> map = [:]
        map.put(mainCapsParams.first().toString(), mainCapsParams.last().toString())
        LOG.debug("Found main capability in system property. Main capability = " + mainCapability)
        return map
    }

    /**
     * Create Device instance
     *
     * @return Device instance
     */
    public static Device createDevice() {
        LOG.debug("Creating new device with main capability.")
        return createDevice(readMainCapability())
    }

    /**
     * Create Device instance with custom device id
     *
     * @param String customDeviceId
     * @return Device instance
     */
    public static Device createDevice(String customDeviceId) {
        LOG.debug("Creating device with custom device id: " + customDeviceId + " and main capability")
        return createDevice(customDeviceId, readMainCapability())
    }

    /**
     * Create Device instance with capability
     *
     * @param String capability
     * @param String value
     * @return Device instance
     */
    public static Device createDevice(String key, String value) {
        LOG.debug("Creating device with capability: " + key + "=" + value + " and random device uuid.")
        Map <String,String> capability = [:]
        capability.put(key,value)
        return createDevice(generateRandomDeviceId(), capability)
    }
    
    /**
     * Create Device instance with capability
     *
     * @param Map < String , String >  capability
     * @return Device instance
     */
    public static Device createDevice(Map<String, String> capability) {
        LOG.debug("Creating device with capability: " + capability + " and random device uuid.")
        return createDevice(generateRandomDeviceId(), capability)
    }

    /**
     * Create Device instance with capability and device id
     *
     * @param String deviceId
     * @param Map < String , String >  - capability
     * @return Device instance
     */
    public static Device createDevice(String deviceId, Map<String, String> capability) {
        LOG.debug("Creating device with capability: " + capability + " and device id:" + deviceId)
        Map<String, String> desiredCapabilities = search(capability, getInstance().capabilities as List)
        if (desiredCapabilities.isEmpty()) {
            throw new GroovyException("Desired capability: " + capability + " not found")
        }
        Device device = new Device(desiredCapabilities)
        getInstance().devices.put(deviceId, device)
        LOG.debug("Created device with capability: " + capability + " and device id:" + deviceId)
        return getActiveDevice(deviceId)
    }

    /**
     * Get active Device instance
     *
     * @return Device instance
     */
    public static Device getActiveDevice() {
        LOG.debug("Getting active device.")
        String deviceId
        if (getInstance().devices.isEmpty()) {
            throw new GroovyException("No active devices found")
        }
        deviceId = getInstance().devices.entrySet().first().key

        return getActiveDevice(deviceId)
    }

    /**
     * Get active Device instance with given deviceId
     *
     * @param deviceId
     * @return Device instance
     */
    public static Device getActiveDevice(String deviceId) {
        LOG.debug("Getting active device with deviceId: " + deviceId)
        Device device
        if (getInstance().devices.isEmpty()) {
            throw new GroovyException("No active devices found")
        }
        device = getInstance().devices[deviceId]

        if (device == null) {
            throw new GroovyException("Active device with deviceId: " + deviceId + " not found")
        }

        LOG.debug("Returned active device. deviceId: " + deviceId)
        return device
    }

    /**
     * Gets all active devices
     *
     * @return Map < String , Device >  - all active devices
     * */
    public static Map<String, Device> getAllActiveDevices() {
        LOG.debug("Getting active devices. Active devices: " + getInstance().devices)
        return getInstance().devices
    }

    /**
     * Quits all active devices
     */
    public static void quitDevices() {
        LOG.debug("Quitting all the active devices")
        getAllActiveDevices().each {
            deviceId, device ->
                device.quitDriver()
        }
        getInstance().devices.clear()
    }

    /**
     * Quits device with deviceId
     *
     * @param deviceId
     * */
    public static void quitDevice(String deviceId) {
        LOG.debug("Quitting device with id: " + deviceId)
        Device device = getActiveDevice(deviceId)
        if (device != null) {
            device.quitDriver()
            getInstance().devices.remove(deviceId)
        }
    }

    /**
     * Searches desired capability with specific parameters from list
     *
     * @param parameters - map of parameters
     * @param capabilitiesList - list of capabilities
     * @return DesiredCapabilities
     * */
    private static Map<String, String> search(Map<String, String> parameters,
                                              List<Map<String, String>> capabilitiesList) {

        Map<String, String> capability = capabilitiesList.find {
            boolean matches = true
            parameters.each { pkey, pvalue ->
                if (!it.containsKey(pkey) || it.get(pkey).toLowerCase() != pvalue.toLowerCase()) {
                    matches = false
                }
            }
            return matches
        }

        if (capability == null) {
            capability = [:]
        }
        return capability
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

    /**
     * Checks if device with device id is already opened
     *
     * @param deviceId
     * */
    public static boolean hasActiveDevice(String deviceId) {
        boolean hasDevice = true
        try {
            getActiveDevice(deviceId)
        } catch (ignored) {
            hasDevice = false
        }
        return hasDevice
    }
}

