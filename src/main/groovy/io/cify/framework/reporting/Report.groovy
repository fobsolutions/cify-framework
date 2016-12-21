package io.cify.framework.reporting

import groovy.json.JsonBuilder
import java.text.DecimalFormat

/**
 * Created by FOB Solutions
 *
 * This class is responsible for test run reporting
 */
class Report extends TestReportManager{

    private static String reportingDirectory = "build/cify/reporting/"
    private static long milli = 1000
    private static DecimalFormat decimalFormat = new DecimalFormat("0.000")
    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

    /**
     * Builds and save json object with test step report information
     * @param step
     * @param scenarioId
     */
    static void reportStep(Step step, String scenarioId){
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.step(testSuiteId:getInstance().testSuiteId, testRunId:activeTestRun.testRunId,
                scenarioId:scenarioId, stepId: step.stepId, name: step.name,
                actions:step.stepActionsList.collect {[actionId:it.actionId, name:it.name, duration:formatDuration(it.duration)]},
                duration: formatDuration(step.duration), result: step.result, errorMessage: formatErrorMessage(step.errorMessage))
        println(jsonBuilder.toPrettyString())
        saveReportToFile(jsonBuilder.toPrettyString(),reportingDirectory + getInstance().testSuiteId + "/" + step.stepId + ".json")
    }

    /**
     * Builds and save json object with test scenario report information
     * @param scenario
     */
    static void reportScenario(Scenario scenario){
        def deviceList = scenario.deviceList
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.scenario(testSuiteId:getInstance().testSuiteId, testRunId:activeTestRun.testRunId,
                scenarioId:scenario.scenarioId, name: scenario.name,startDate: formatDate(scenario.startDate),
                devices:deviceList.collect {[deviceId:it.deviceId, deviceCategory:it.deviceCategory]},
                steps:scenario.stepList.collect {[stepId:it.stepId, stepName:it.name, stepResult:it.result]},
                endDate: formatDate(scenario.endDate),
                duration: formatDuration(scenario.duration), result: scenario.result, errorMessage: formatErrorMessage(scenario.errorMessage))
        println(jsonBuilder.toPrettyString())
        saveReportToFile(jsonBuilder.toPrettyString(),reportingDirectory + getInstance().testSuiteId + "/" + scenario.scenarioId + ".json")
    }

    /**
     * Builds and save json object with test run report information
     * @param testRun
     */
    public static void reportTestRun(TestRun testRun){
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.testrun(testSuiteId:getInstance().testSuiteId, testRunId:testRun.testRunId,
                name: testRun.name, startDate: formatDate(testRun.startDate),
                scenarios:testRun.scenarioList.collect {[scenarioId:it.scenarioId, scenarioName:it.name, scenarioResult:it.result]},
                endDate: formatDate(testRun.endDate),
                duration: formatDuration(testRun.duration), result: testRun.result)
        println(jsonBuilder.toPrettyString())
        saveReportToFile(jsonBuilder.toPrettyString(),reportingDirectory + getInstance().testSuiteId + "/" + testRun.testRunId + ".json")
    }

    private static String formatDate(long dateInMillisecends){
        return new Date(dateInMillisecends).format(dateFormat)
    }

    private static String formatErrorMessage(String errorMessage){
        return errorMessage? errorMessage : ""
    }

    private static String formatDuration(long duration){
        return duration? decimalFormat.format(duration / milli) : 0
    }

    private static void saveReportToFile(String content, String filePath) {
        def file = new File(filePath)
        file.getParentFile().mkdirs()
        file.write(content)
    }
}
