package io.cify.framework.reporting

import groovy.json.JsonBuilder

import java.text.DecimalFormat

class Report extends TestReportManager{

    static long millis = 1000
    static DecimalFormat df = new DecimalFormat("#.00");
    static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

    public static void reportStep(Step step, String scenarioId){
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.step(testSuiteId:getInstance().testSuiteId, testRunId:activeTestRun.testRunId,
                scenarioId:scenarioId, stepId: step.stepId, name: step.name,
                actions:step.stepActionsList.collect {[actionId:it.actionId, name:it.name, duration:df.format(it.duration / millis)]},
                duration: df.format(step.duration / millis), result: step.result, errorMessage: step.errorMessage)
        println(jsonBuilder.toPrettyString())
    }

    public static void reportScenario(Scenario scenario){
        def deviceList = scenario.deviceList
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.scenario(testSuiteId:getInstance().testSuiteId, testRunId:activeTestRun.testRunId,
                scenarioId:scenario.scenarioId, name: scenario.name,
                devices:deviceList.collect {[deviceId:it.deviceId, deviceCategory:it.deviceCategory]},
                startDate: new Date(scenario.startDate).format(dateFormat), endDate: new Date(scenario.endDate).format(dateFormat),
                duration: df.format(scenario.duration / millis), result: scenario.result, errorMessage: scenario.errorMessage)
        println(jsonBuilder.toPrettyString())
    }

    public static void reportTestRun(TestRun testRun){
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.testrun(testSuiteId:getInstance().testSuiteId, testRunId:testRun.testRunId,
                name: testRun.name, startDate: new Date(testRun.startDate).format(dateFormat), endDate: new Date(testRun.endDate).format(dateFormat),
                duration: df.format(testRun.duration / millis), result: testRun.result)
        println(jsonBuilder.toPrettyString())
    }
}
