package io.cify.framework.core

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.openqa.selenium.remote.DesiredCapabilities
import groovy.util.logging.Slf4j
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * Created by FOB Solution
 *
 * This class holds device capabilities
 */

@Slf4j
class Capabilities {

    private static final Marker MARKER = MarkerFactory.getMarker('CAPABILITIES') as Marker

    private LazyMap android = [:]
    private LazyMap ios = [:]
    private LazyMap browser = [:]

    /**
     * Parses capabilities json to Capabilities
     *
     * @param string json string
     *
     * @return Capabilities
     */
    public static Capabilities parseFromJsonString(String capabilitiesJson) {
        log.debug(MARKER, "Parse capabilities from json string \n $capabilitiesJson")
        return new JsonSlurper().parseText(capabilitiesJson) as Capabilities
    }

    /**
     * Converts capabilities to json string
     *
     * @return String
     */
    public String toPrettyString() {
        log.debug(MARKER, "Converts capabilities to json string")
        return new JsonBuilder(this).toPrettyString()
    }

    /**
     * Returns desired capabilities for selected device category
     *
     * @param category device category
     *
     * @return DesiredCapabilities
     */
    public DesiredCapabilities toDesiredCapabilities(DeviceCategory category) {
        log.debug(MARKER, "Get desired capabilities for category $category")
        switch (category) {
            case DeviceCategory.BROWSER:
                return new DesiredCapabilities(browser)
            case DeviceCategory.ANDROID:
                return new DesiredCapabilities(android)
            case DeviceCategory.IOS:
                return new DesiredCapabilities(ios)
        }
        throw new CifyFrameworkException("Unsupported device category $category")
    }

}