package io.cify.framework.runner.utils

import gherkin.formatter.FilterFormatter
import gherkin.formatter.JSONFormatter
import gherkin.parser.Parser
import gherkin.util.FixJava
import groovy.json.JsonSlurper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger

/**
 * This parser is responsible for reading and filtering Gherkin files
 *
 * Created by FOB Solutions
 */

class GherkinFeatureParser {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('GHERKIN PARSER') as Marker

    /**
     * Checks if feature file has scenarios
     *
     * @param filePath a path to feature file
     * @param filters list of filters to apply on gherkin string
     * @return boolean
     * */
    static boolean hasScenarios(String filePath, List filters) {
        LOG.debug(MARKER, "Check for scenarios matching filters $filters in feature file $filePath")
        String gherkin = readFile(filePath)
        gherkin ? hasScenarios(gherkin, filePath, filters) : false
    }

    /**
     * Checks if gherkin string has scenarios
     *
     * @param gherkin a gherkin string
     * @param filePath a path to feature file
     * @param filters filters to apply on gherkin string
     *
     * @return boolean
     * */
    static boolean hasScenarios(String gherkin, String filePath, List filters) {
        LOG.debug(MARKER, "Check for scenarios matching filters $filters in gherkin ($filePath) \n $gherkin")
        getScenarios(gherkin, filePath, filters)
    }

    /**
     * Gets scenarios/scenario outlines in gherkin
     *
     * @param gherkin a gherkin string
     * @param filePath a path to feature file
     * @param filters filters to apply on gherkin string
     * @return the number of scenarios in gherkin
     * */
    static List<Object> getScenarios(String gherkin, String filePath, List filters) {
        List<Object> result = []
        Object json = gherkinToJson(gherkin, filePath, filters)
        if (json && json.elements && json.elements.size() > 0) {
            result = GherkinFeatureFilter.filterScenarios(json, filters).elements
        }
        result
    }

    private static String readFile(String filePath) {
        LOG.debug(MARKER, "Read file $filePath")
        return FixJava.readReader(new InputStreamReader(new FileInputStream(filePath), 'UTF-8'))
    }

    /**
     * Converts gherkin to JSON
     *
     * @param gherkin a gherkin string
     * @param featurePath a path to feature file
     * @param filters filters to apply on gherkin string
     * @return JSON object
     * */
    private static Object gherkinToJson(String gherkin, String featurePath, List filters) {
        LOG.debug(MARKER, "Parse gherkin to json. \n $gherkin \n $featurePath \n $filters")
        try {
            StringBuilder json = new StringBuilder()

            JSONFormatter formatter = new JSONFormatter(json)
            Parser parser = new Parser(formatter)

            if (filters) {
                FilterFormatter filterFormatter = new FilterFormatter(formatter, filters)
                parser = new Parser(filterFormatter)
            }

            parser.parse(gherkin, featurePath ?: '', 0)
            formatter.done()
            formatter.close()

            return new JsonSlurper().parseText(json.toString())
        } catch (all) {
            LOG.warn("Failed to parse gherkin to JSON: $featurePath. \n Cause: $all.message", all)
        }
        null
    }
}