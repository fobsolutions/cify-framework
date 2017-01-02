package io.cify.framework.reporting

import groovy.json.JsonBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger

import java.text.DecimalFormat

/**
 * Created by FOB Solutions
 *
 * This class is responsible for test run reporting
 */
class Report extends TestReportManager {
    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('REPORT') as Marker

    private static long milli = 1000
    private static DecimalFormat decimalFormat = new DecimalFormat("0.000")
    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

    /**
     * Builds json object with test step report information
     * @param step
     * @param scenarioId
     * @return json pretty string
     */
    static String reportStep(Step step, String scenarioId) {
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.step(
                testSuiteId: testSuiteId,
                testRunId: activeTestRun.testRunId,
                scenarioId: scenarioId,
                stepId: step.stepId,
                name: step.name,
                actions: step.stepActionsList.collect {
                    [
                            actionId: it.actionId,
                            name    : it.name,
                            duration: formatDuration(it.duration)
                    ]
                },
                duration: formatDuration(step.duration), result: step.result, errorMessage: formatErrorMessage(step.errorMessage))
        LOG.info(MARKER, jsonBuilder.toPrettyString())
        return jsonBuilder.toPrettyString()
    }

    /**
     * Builds json object with test scenario report information
     * @param scenario
     * @return json pretty string
     */
    static String reportScenario(Scenario scenario) {
        def deviceList = scenario.deviceList
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.scenario(
                testSuiteId: testSuiteId,
                testRunId: activeTestRun.testRunId,
                scenarioId: scenario.scenarioId,
                name: scenario.name,
                startDate: formatDate(scenario.startDate),
                devices: deviceList.collect { it },
                steps: scenario.stepList.collect {
                    [
                            stepId    : it.stepId,
                            stepName  : it.name,
                            stepResult: it.result
                    ]
                },
                endDate: formatDate(scenario.endDate),
                duration: formatDuration(scenario.duration), result: scenario.result, errorMessage: formatErrorMessage(scenario.errorMessage))
        LOG.info(MARKER, jsonBuilder.toPrettyString())
        return jsonBuilder.toPrettyString()
    }

    /**
     * Builds json object with test run report information
     * @param testRun
     * @return json pretty string
     */
    static String reportTestRun(TestRun testRun) {
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.testrun(
                testSuiteId: testSuiteId,
                testRunId: testRun.testRunId,
                name: testRun.name,
                featureId: testRun.featureId,
                startDate: formatDate(testRun.startDate),
                projectName: testRun.projectName,
                suiteName: testRun.suiteName,
                capabilitiesId: testRun.capabilitiesId,
                scenarios: testRun.scenarioList.collect {
                    [
                            scenarioId    : it.scenarioId,
                            scenarioName  : it.name,
                            scenarioResult: it.result,
                            cucumberId    : it.cucumberScenarioId
                    ]
                },
                endDate: formatDate(testRun.endDate),
                duration: formatDuration(testRun.duration), result: testRun.result)
        LOG.info(MARKER, jsonBuilder.toPrettyString())
        return jsonBuilder.toPrettyString()
    }

    private static String formatDate(long dateInMilliseconds) {
        return new Date(dateInMilliseconds).format(dateFormat)
    }

    private static String formatErrorMessage(String errorMessage) {
        return errorMessage ? errorMessage : ""
    }

    private static String formatDuration(long duration) {
        return duration ? decimalFormat.format(duration / milli) : 0
    }
}
