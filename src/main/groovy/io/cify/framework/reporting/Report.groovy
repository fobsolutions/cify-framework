package io.cify.framework.reporting

import groovy.json.JsonBuilder

import java.text.DecimalFormat

class Report extends TestRunManager{

    static long nanos = 1000000000
    static long millis = 1000
    static DecimalFormat df = new DecimalFormat("#.00");

    public static void reportStep(Step step, String scenarioId){
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.step(testSuiteId:getInstance().testSuiteId, testRunId:activeTestRun.testRunId,
                scenarioId:scenarioId, stepId: step.stepId, name: step.name,
                duration: df.format(step.duration / nanos), result: step.result, errorMessage: step.errorMessage)
        println(jsonBuilder.toPrettyString())
    }

    public static void reportScenario(Scenario scenario){
        def deviceList = scenario.deviceList
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.scenario(testSuiteId:getInstance().testSuiteId, testRunId:activeTestRun.testRunId,
                scenarioId:scenario.scenarioId, name: scenario.name,
                devices:deviceList.collect {[deviceId:it.deviceId, deviceCategory:it.deviceCategory]},
                duration: df.format(scenario.duration / millis), result: scenario.result, errorMessage: scenario.errorMessage)
        println(jsonBuilder.toPrettyString())
    }

    public static void reportTestRun(TestRun testRun){
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.testrun(testSuiteId:getInstance().testSuiteId, testRunId:testRun.testRunId,
                name: testRun.name, duration: df.format(testRun.duration / millis), result: testRun.result)
        println(jsonBuilder.toPrettyString())
    }
}
