package io.cify.framework.reporting

import groovy.json.JsonBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger

/**
 * Created by FOB Solutions
 *
 * This class is responsible for test run reporting
 */
class Report extends TestReportManager {
    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('REPORT') as Marker

    /**
     * Builds json object with test step report information
     * @param step
     * @param scenarioId
     * @return json string
     */
    static String reportStep(Step step, String scenarioId) {
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.step(
                projectName: activeTestRun.projectName,
                testsuiteId: testsuiteId,
                suiteName: activeTestRun.suiteName,
                testrunId: activeTestRun.testrunId,
                cucumberTestrunId: activeTestRun.cucumberTestrunId,
                scenarioId: scenarioId,
                scenarioName: activeScenario.name,
                cucumberScenarioId: activeScenario.cucumberScenarioId,
                stepId: step.stepId,
                name: step.name,
                startDate: step.startDate,
                actions: step.stepActionsList.collect {
                    [
                            actionId: it.actionId,
                            name    : it.name,
                            duration: it.duration
                    ]
                },
                endDate: step.endDate,
                duration: step.duration, result: step.result, errorMessage: formatErrorMessage(step.errorMessage))
        LOG.info(MARKER, jsonBuilder.toPrettyString())
        return jsonBuilder.toString()
    }

    /**
     * Builds json object with test scenario report information
     * @param scenario
     * @return json string
     */
    static String reportScenario(Scenario scenario) {
        def deviceList = scenario.deviceList
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.scenario(
                projectName: activeTestRun.projectName,
                testsuiteId: testsuiteId,
                suiteName: activeTestRun.suiteName,
                testrunId: activeTestRun.testrunId,
                cucumberTestrunId: activeTestRun.cucumberTestrunId,
                scenarioId: scenario.scenarioId,
                scenarioName: scenario.name,
                cucumberScenarioId: scenario.cucumberScenarioId,
                startDate: scenario.startDate,
                passedSteps: scenario.stepList.findAll { it.result == "passed" }.size(),
                failedSteps: scenario.stepList.findAll { it.result == "failed" }.size(),
                skippedSteps: scenario.stepList.findAll { it.result == "skipped" }.size(),
                devices: deviceList.collect { it },
                steps: scenario.stepList.collect {
                    [
                            stepId          : it.stepId,
                            stepName        : it.name,
                            stepResult      : it.result,
                            actions         : it.stepActionsList.collect {
                                [
                                        actionId      : it.actionId,
                                        actionName    : it.name,
                                        actionDuration: it.duration
                                ]
                            },
                            stepDuration    : it.duration,
                            stepErrorMessage: formatErrorMessage(it.errorMessage)
                    ]
                },
                endDate: scenario.endDate,
                duration: scenario.duration, result: scenario.result, errorMessage: formatErrorMessage(scenario.errorMessage))
        LOG.info(MARKER, jsonBuilder.toPrettyString())
        return jsonBuilder.toString()
    }

    /**
     * Builds json object with test run report information
     * @param testRun
     * @return json string
     */
    static String reportTestRun(TestRun testRun) {
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.testrun(
                projectName: testRun.projectName,
                testsuiteId: testsuiteId,
                suiteName: testRun.suiteName,
                testrunId: testRun.testrunId,
                testrunName: testRun.name,
                cucumberTestrunId: testRun.cucumberTestrunId,
                startDate: testRun.startDate,
                capabilitiesId: testRun.capabilitiesId,
                passedScenarios: testRun.scenarioList.findAll { it.result == "passed" }.size(),
                failedScenarios: testRun.scenarioList.findAll { it.result == "failed" }.size(),
                passedSteps: sumOfAllStepsInTestrun(testRun, "passed"),
                failedSteps: sumOfAllStepsInTestrun(testRun, "failed"),
                skippedSteps: sumOfAllStepsInTestrun(testRun, "skipped"),
                scenarios: testRun.scenarioList.collect {
                    [
                            scenarioId        : it.scenarioId,
                            scenarioName      : it.name,
                            scenarioResult    : it.result,
                            cucumberScenarioId: it.cucumberScenarioId,
                            devices           : it.deviceList.collect { it }
                    ]
                },
                testrunDevices: getUniqueDeviceList(testRun).size(),
                testrunDeviceList: getUniqueDeviceList(testRun),
                endDate: testRun.endDate,
                duration: testRun.duration, result: testRun.result)
        LOG.info(MARKER, jsonBuilder.toPrettyString())
        return jsonBuilder.toString()
    }

    private static List getUniqueDeviceList(TestRun testRun) {
        List list = new ArrayList<>()
        testRun?.scenarioList?.each {
            it.deviceList?.each {
                list.add(it)
            }
        }
        return list.unique()
    }

    private static int sumOfAllStepsInTestrun(TestRun testRun, String status) {
        int result = 0
        testRun?.scenarioList?.each { result = result + it.stepList.findAll { it.result == status }.size() }
        return result
    }

    private static String formatErrorMessage(String errorMessage) {
        return errorMessage ? errorMessage : ""
    }

}
