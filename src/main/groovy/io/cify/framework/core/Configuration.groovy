package io.cify.framework.core

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap

/**
 * Created by FOB Solutions
 *
 * This class is responsible for configuration management
 */
class Configuration {

    private static final String CONFIGURATION_FILE = "configuration.json"

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
            String value = System.getProperty(it.getKey() as String, JsonOutput.toJson(it.getValue()))
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
        if (!configurationFile.exists()) {
            throw new FileNotFoundException("Cannot find configuration file! Please add configuration.json to project root!")
        }
        return new JsonSlurper().parseText(configurationFile.text) as LazyMap
    }
}