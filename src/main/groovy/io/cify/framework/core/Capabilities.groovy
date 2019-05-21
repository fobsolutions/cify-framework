package io.cify.framework.core

import groovy.json.JsonBuilder
import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * Created by FOB Solution
 *
 * This class holds device capabilities
 */

class Capabilities {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CAPABILITIES') as Marker

    private List<LazyMap> capabilityList = []

    private Capabilities(List capsList) {
        capabilityList = capsList
    }

    /**
     * Parses capabilities json to Capabilities
     *
     * @param string json string
     *
     * @return Capabilities
     */
    static Capabilities parseFromJsonString(String capabilitiesJson) {

        def json = new JsonSlurper().parseText(capabilitiesJson)
        List<Map> parsedList = []
        try {
            json.each {
                category, capabilitiesList ->
                    capabilitiesList.eachWithIndex { caps, index ->
                        if (!caps.capabilityId)
                            caps.put('capabilityId', category + index + caps.hashCode())

                        Map map = [id: caps.capabilityId, category: category, capabilities: caps, available: true]
                        parsedList.add(map)
                    }
            }
        } catch (ignore) {
            throw new JsonException("Error occurred while parsing capabilities. Check the structure of your capabilities.json/configuration.json.")
        }
        return new Capabilities(parsedList)
    }

    /**
     * Converts capabilities list to json string
     *
     * @return String
     */
    String toPrettyString() {
        LOG.debug(MARKER, "Converts capabilities to json string")
        return new JsonBuilder(capabilityList).toPrettyString()
    }

    /**
     * Returns desired capabilities for selected device category
     *
     * @param category device category
     *
     * @return DesiredCapabilities
     */
    DesiredCapabilities toDesiredCapabilities(DeviceCategory category) {
        LOG.debug(MARKER, "Get desired capabilities for category $category")
        Map result = [:]
        try {
            result = capabilityList.find {
                (it.category as String).toUpperCase() == category.toString() && it.available
            }.capabilities as LazyMap
        } catch (NullPointerException) {
            new CifyFrameworkException("No available capabilities for $category found")
        }
        return new DesiredCapabilities(result)
    }

    /**
     * Returns desired capabilities for given capabilityId
     *
     * @param capabilityId capabilities identifier
     * @return DesiredCapabilities
     */
    DesiredCapabilities toDesiredCapabilities(String capabilityId) {
        LOG.debug(MARKER, "Get desired capabilities for capabilityID $capabilityId")
        Map result = [:]
        try {
            result = capabilityList.find { it.id == capabilityId && it.available }.capabilities as LazyMap
        } catch (NullPointerException) {
            new CifyFrameworkException("No available capabilities for $capabilityId found")
        }
        return new DesiredCapabilities(result)
    }

    /**
     * Adds to desired capabilities
     *
     * @param category - Device category
     * @param key - key to add
     * @param value - value to add
     */
    void addToDesiredCapabilities(DeviceCategory category, String key, String value) {
        LOG.debug(MARKER, "Add to desired capabilities for category $category")
        try {
            capabilityList.findAll { (it.category as String).toUpperCase() == category.toString() }.each {
                it.capabilities.put(key, value)
            }
        } catch (NullPointerException) {
            throw new CifyFrameworkException("Could not add $key:$value. Capabilities for category $category not found.")
        }
    }

    /**
     * Sets available key of capability with given capabilityId to given value
     *
     * @param capabilityId id of the capability
     *
     * @param value value given to available key
     */
    void setAvailable(String capabilityId, boolean isAvailable) {
        try {
            capabilityList.find { it.id == capabilityId }.available = isAvailable
        } catch (NullPointerException) {
            throw new CifyFrameworkException("Setting \"available\":$isAvailable failed. Capability with capabilityId:$capabilityId not found")

        }
    }

    /**
     * Gets category from capability with given id
     * @param capabilityId id of the capability
     * @return
     */
    DeviceCategory getCategory(String capabilityId) {
        try {
            DeviceCategory.valueOf((capabilityList.find { it.id == capabilityId }.category as String).toUpperCase())
        } catch (NullPointerException) {
            throw new CifyFrameworkException("No category found for capabilityId:$capabilityId")
        }
    }
}