package io.cify.framework.reporting

import groovy.json.JsonBuilder

import java.text.DecimalFormat
import static java.util.UUID.randomUUID

class TestRunManager {

    public testSuiteId
    static TestRun activeTestRun
    private static volatile TestRunManager instance

    public TestRunManager(){
    }

    public static TestRunManager getInstance() {
        if (instance == null) {
            synchronized (TestRunManager.class) {
                if (instance == null) {
                    instance = new TestRunManager()
                }
            }
        }
        return instance
    }

    public static TestRunManager getTestRunManager(){
        return getInstance()
    }

    public static void testRunStarted(String name, String testSuiteId){
        getInstance().testSuiteId = testSuiteId
        activeTestRun = new TestRun(name)
    }

    public static void scenarioStarted(String name){
        TestRun testrun = activeTestRun
        if( testrun == null) {return}
        testrun.scenarioList.add(new Scenario(name))
        testrun.activeScenario = testrun.scenarioList.last()
    }

    public static void stepStarted(String name){
        Scenario scenario = activeTestRun.activeScenario
        if(scenario == null) {return}
        scenario.stepList.add(new Step(name))
    }

    public static void testRunFinished(){
        activeTestRun.endDate = System.currentTimeMillis()
        activeTestRun.duration = activeTestRun.endDate - activeTestRun.startDate
        activeTestRun.result = activeTestRun.scenarioList.findResult { Scenario s ->
            if(s.result == "failed"){ return "failed"}
            else { return "passed"}
        }
        Report.reportTestRun(activeTestRun)
        activeTestRun = null
    }

    public static void scenarioFinished(String result, String errorMessage){
        Scenario scenario = activeTestRun.activeScenario
        if(scenario != null) {
            scenario.endDate = System.currentTimeMillis()
            scenario.duration = scenario.endDate - scenario.startDate
            scenario.errorMessage = errorMessage
            scenario.result = result
            Report.reportScenario(scenario)
        }
    }

    public static void stepFinished(String result, long duration, String errorMessage){
        Scenario currentScenario = activeTestRun.activeScenario
        Step currentStep = currentScenario.stepList.findResult { Step s ->
            if(s.result == null){ return s }
            else{ return null }
        }
        if(currentStep != null) {
            currentStep.errorMessage = errorMessage
            currentStep.duration = duration
            currentStep.result = result
            Report.reportStep(currentStep,currentScenario.scenarioId)
        }
    }

    public static addDeviceToTestReport(String deviceId, String deviceCategory){
        Scenario scenario = getActiveScenario()
        if(scenario != null ) {
            Map<String, String> device = new HashMap<>()
            device.put("deviceId", deviceId)
            device.put("deviceCategory", deviceCategory)
            scenario.deviceList.add(device)
        }
    }

    private static Scenario getActiveScenario(){
        if(activeTestRun == null ) {return null}
        Scenario scenario = activeTestRun.activeScenario
        return scenario
    }

    void report(){
        long nanos = 1000000000
        long millis = 1000
        DecimalFormat df = new DecimalFormat("#.00");

        println("TODO: report here.................................................................")
        println("testSuiteId:"+ testSuiteId)
        activeTestRun.with {
            println("testRunId:" + it.testRunId + " - - feature start: " + it.name + " date:" + new Date(it.startDate))
            it.scenarioList.each {
                println("scenarioId:" + it.scenarioId + " - - - scenario start: " + it.name)
                it.deviceList.each {
                    println(" - - - scenario device category: " + it.deviceCategory + " deviceId: " + it.deviceId)
                }
                it.stepList.each {
                    println("stepId:" + it.stepId + " - - - - step: " + it.name)
                    println("stepId:" + it.stepId + " " + it.result + " duration(sec):" + df.format(it.duration / nanos) + "message: "+ it.errorMessage)
                }
                println("scenarioId:" + it.scenarioId + " - - - scenario end: " + it.name)
                println("scenarioId:" + it.scenarioId + " " + it.result + " duration(sec):" + df.format(it.duration / millis))
            }
            println("testRunId:" + it.testRunId + " - - feature end: " + it.name)
            println("testRunId:" + it.testRunId + " " + it.result + " duration(sec):" + df.format(it.duration / millis) + " date:" + new Date(it.endDate))
        }

    }

    public static String generateId(){
        return randomUUID() as String
    }
}
