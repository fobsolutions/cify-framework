package io.cify.framework.core

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger

/**
 * Created by FOB Solutions
 *
 * This class is responsible for configuration management
 */
class Configuration {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CONFIGURATION') as Marker


    private static final String CONFIGURATION_FILE = "configuration.json"
    private static final String TASK_NAME = "task"

    /**
     * Set framework parameters to system properties
     * */
    public static void setupFrameworkConfiguration() {
        mergeCommandLindAndFileContent()
    }

    /**
     * Merge command line with configuration json and set to system properties
     * */
    private static void mergeCommandLindAndFileContent() {
        LazyMap configMap = readFrameworkConfigurationFile()
        configMap.each {
            String value
            if (it.getKey() == "capabilities") {
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
        File configurationFile = new File(CONFIGURATION_FILE)
        if (System.getProperty(TASK_NAME)) {
            LOG.warn(MARKER, "Using configuration parameters from Cify-Runner...")
            return [:]
        } else {
            if (!configurationFile.exists()) {
                throw new FileNotFoundException("Cannot find configuration file! Please add configuration.json to project root!")
            }
            return new JsonSlurper().parseText(configurationFile.text) as LazyMap
        }
    }
}