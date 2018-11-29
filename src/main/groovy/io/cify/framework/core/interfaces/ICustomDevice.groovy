package io.cify.framework.core.interfaces

import io.cify.framework.core.DeviceCategory

/**
 * Created by FOB Solutions
 */
interface ICustomDevice {

    /**
     * Gets device ID
     * */
    String getId()

    /**
     * Is device active
     * @return
     */
    boolean isActive()

    /**
     * Set device active
     * @param active
     */
    void setActive(boolean active)

    /**
     * Run command from client
     * @param closure
     * @return
     */
    def runCommand(Closure closure)

    /**
     * Gets device category
     * */
    DeviceCategory getCategory()

    /**
     * Sets capability to device
     * */
    void setCapability(String key, String value)

    /**
     * Gets device capabilities
     * @return capabilities
     */
    def getCapabilities()

    /**
     * Closes the device
     */
    void quit()

}