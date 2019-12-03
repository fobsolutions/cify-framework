package io.cify.framework.core

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger

import static io.cify.common.CifyConstants.*

/**
 * Created by FOB Solutions
 *
 * This class is responsible for configuration management
 */
class Configuration {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CONFIGURATION') as Marker

    /**
     * Set framework parameters to system properties
     * */
    static void setupFrameworkConfiguration() {
        mergeCommandLindAndFileContent()
    }

    /**
     * Merge command line with configuration json and set to system properties
     * */
    private static void mergeCommandLindAndFileContent() {
        LazyMap configMap = readFrameworkConfigurationFile()
        configMap.each {
            String value
            if (it.getKey() == SYSTEM_PROPERTY_CAPABILITIES || it.getKey() == SYSTEM_PROPERTY_CREDENTIALS) {
                value = System.getProperty(it.getKey() as String, JsonOutput.toJson(it.getValue()))
            } else {
                value = System.getProperty(it.getKey() as String, it.getValue() as String)
            }
            System.setProperty(it.getKey() as String, value)
        }
    }

    /**
     * Read configuration json from file
     *
     * @return LazyMap - Configuration map
     * */
    private static LazyMap readFrameworkConfigurationFile() {
        if (System.getProperty(SYSTEM_PROPERTY_TASK_NAME)) {
            LOG.warn(MARKER, "Using configuration parameters from Cify-Runner...")
            return [:]
        } else {
            File configurationFile = new File(System.getProperty("CONFIG_FILE_PREFIX", "") + CIFY_CONFIG_FILE_NAME)
            if (!configurationFile.exists()) {
                throw new FileNotFoundException("Cannot find configuration file! Please add $CIFY_CONFIG_FILE_NAME to project root!")
            }
            return new JsonSlurper().parseText(configurationFile.text) as LazyMap
        }
    }
}
