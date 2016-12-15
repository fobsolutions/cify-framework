package io.cify.framework.reporting

import static java.util.UUID.randomUUID

class TestRunManager {

    public String id
    public Date startDate
    public Date endDate

    TestRun activeTestRun
    static List<TestRun> testRunList
    private static volatile TestRunManager instance

    private TestRunManager(){
        this.id = generateId()
        this.startDate = new Date()
        testRunList = new ArrayList<>()
    }

    public static String generateId(){
        return randomUUID() as String
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

    public static TestRunManager testSuiteStarted(){
        println("testSuiteStarted")
        return getInstance()
    }

    public void testSuiteFinished(){
        println("testSuiteFinished")
        if(testRunList == null) {return}
        endDate = new Date()

        println("TODO: report here.................................................................")
        println("id:" + getInstance().id + " - test suite started")
        testRunList.each {
            testRunList.each {
                println("id:" + it.id +  " - - feature start: " + it.name)
                it.scenarioList.each {
                    println("id:" + it.id + " - - - scenario start: " + it.name)
                    it.stepList.each {
                        if (it.deviceId != null) {
                            println("id:" + it.id +  " - - - - step: " + it.name + " category:" + it.deviceCategory + " deviceId:" + it.deviceId)
                        } else {
                            println("id:" + it.id +  " - - - - step: " + it.name)
                        }
                        println("id:" + it.id + " " + it.result + " duration:"+it.duration)
                    }
                    println("id:" + it.id + " - - - scenario end: " + it.name)
                    println("id:" + it.id + " " + it.result +  " duration:"+it.duration)
                }
                println("id:" + it.id + " - - feature end: " + it.name)
                println("id:" + it.id + " " + it.result)
            }
        }
    }

    public void testRunStarted(String name){
        println("testRunStarted - feature: "+ name)
        getInstance().testRunList.add(new TestRun(name))
        activeTestRun = testRunList.last()
    }

    public void scenarioStarted(String name){
        println("scenarioStarted " + name)
        TestRun testrun = getInstance().activeTestRun
        if( testrun == null) {return}

        testrun.scenarioList.add(new Scenario(name))
        testrun.activeScenario = testrun.scenarioList.last()
    }

    public void stepStarted(String name){
        println("stepStarted " + name)
        Scenario scenario = getInstance().activeTestRun.activeScenario
        if(scenario == null) {return}
        scenario.stepList.add(new Step(name))
    }

    public void testRunFinished(String result){
        println("testRunFinished - result " + result)
        activeTestRun.endDate = new Date()
        activeTestRun.result = result
        activeTestRun = null
    }

    public void scenarioFinished(String result, long duration, String errorMessage){
        println("scenarioFinished - result " + result)
        Scenario scenario = getInstance().activeTestRun.activeScenario
        if(scenario == null) {return}
        scenario.endDate = new Date()
        scenario.duration = duration
        scenario.errorMessage = errorMessage
        scenario.result = result
        scenario = null
    }

    public void stepFinished(String result, long duration, String errorMessage){
        println("stepFinished - result " + result )
        Step currentStep = getInstance().activeTestRun.activeScenario.stepList.findResult { Step s ->
            if(s.result == null){ return s }
        }
        if(currentStep == null) {return}
        currentStep.duration = duration
        currentStep.result = result
        currentStep = null
    }

    public static Scenario getActiveScenario(){
        if(getInstance().activeTestRun == null ) {return null}
        Scenario scenario = getInstance().activeTestRun.activeScenario
        println("getActiveScenario - name " + scenario.name)
        return scenario
    }

}
