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
        return getInstance()
    }

    public void testSuiteFinished(){
        if(testRunList == null) {return}
        endDate = new Date()

        println("TODO: report here.................................................................")
        println("id:" + getInstance().id + " - test suite started")
        testRunList.each {
            testRunList.each {
                println("id:" + it.id + " " + it.result + " - - feature name: " + it.name)
                it.scenarioList.each {
                    println("id:" + it.id + " " + it.result +  " - - - scenario name: " + it.name)
                    it.stepList.each {
                        if (it.deviceId != null) {
                            println("id:" + it.id + " " + it.result +  " - - - - step name: " + it.name + " category:" + it.deviceCategory + " deviceId:" + it.deviceId)
                        } else {
                            println("id:" + it.id + " " + it.result +  " - - - - step name: " + it.name)
                        }
                    }
                }
            }
        }
    }

    public void testRunStarted(String name){
        getInstance().testRunList.add(new TestRun(name))
        activeTestRun = testRunList.last()
    }

    public void scenarioStarted(String name){
        TestRun testrun = getInstance().activeTestRun
        if( testrun == null) {return}

        testrun.scenarioList.add(new Scenario(name))
        testrun.activeScenario = testrun.scenarioList.last()
    }

    public void stepStarted(String name){
        Scenario scenario = getInstance().activeTestRun.activeScenario
        if(scenario == null) {return}

        scenario.stepList.add(new Step(name))
        scenario.activeStep = scenario.stepList.last()
    }

    public void testRunFinished(String result){
        activeTestRun.endDate = new Date()
        activeTestRun.result = result
        activeTestRun = null
    }

    public void scenarioFinished(String result){
        Scenario scenario = getInstance().activeTestRun.activeScenario
        if(scenario == null) {return}
        scenario.endDate = new Date()
        scenario.result = result
        scenario = null
    }

    public void stepFinished(String result){
        Step step = getInstance().activeTestRun.activeScenario.activeStep
        if(step == null) {return}
        step.endDate = new Date()
        step.result = result
        step = null
    }

    public static Step getActiveStep(){
        return getInstance().activeTestRun.activeScenario.activeStep
    }

}
