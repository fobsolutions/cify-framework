package io.cify.framework.reporting

import java.text.DecimalFormat
import static java.util.UUID.randomUUID

class TestReportManager {

    public testSuiteId
    static TestRun activeTestRun
    private static volatile TestReportManager instance

    public TestReportManager(){
    }

    public static TestReportManager getInstance() {
        if (instance == null) {
            synchronized (TestReportManager.class) {
                if (instance == null) {
                    instance = new TestReportManager()
                }
            }
        }
        return instance
    }

    public static TestReportManager getTestRunManager(){
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
        Scenario scenario = getActiveScenario()
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
        Scenario scenario = getActiveScenario()
        if(scenario != null) {
            scenario.endDate = System.currentTimeMillis()
            scenario.duration = scenario.endDate - scenario.startDate
            scenario.errorMessage = errorMessage
            scenario.result = result
            Report.reportScenario(scenario)
        }
    }

    public static void stepFinished(String result, long duration, String errorMessage){
        Step currentStep = getActiveStep()
        if(currentStep != null) {
            currentStep.errorMessage = errorMessage
            currentStep.duration = duration
            currentStep.result = result
            Report.reportStep(currentStep,getActiveScenario().scenarioId)
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

    public static void addStepActionsToTestReport(String name){
        Step currentStep = getActiveStep()
        if( currentStep == null) {return}
        currentStep.stepActionsList.add(new StepActions(name))
    }

    private static Scenario getActiveScenario(){
        if(activeTestRun == null ) {return null}
        return activeTestRun.activeScenario
    }

    private static Step getActiveStep(){
        if(getActiveScenario() == null || getActiveScenario().stepList.size() == 0 ) {return null}
        Step currentStep = getActiveScenario().stepList.findResult { Step s ->
            if(s.result == null){ return s }
            else{ return null }
        }
        return currentStep
    }

    public static String generateId(){
        return randomUUID() as String
    }
}
