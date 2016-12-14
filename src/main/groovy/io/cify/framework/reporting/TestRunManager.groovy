package io.cify.framework.reporting

import static java.util.UUID.randomUUID

class TestRunManager {

    public String id
    TestRun activeTestRun
    static List<TestRun> testRunList
    private static volatile TestRunManager instance

    private TestRunManager(){
        this.id = generateId()
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

    public static void testSuiteFinished(){
        if(getInstance().testRunList == null) {return}

        // add test suite end time
        println("TODO: report here.................................................................")
        println("test suite id:"+ getInstance().id)
        getInstance().testRunList.each {
            testRunList.each {
                println("- feature name:" + it.name + " id:" + it.id)
                it.scenarioList.each {
                    println("-- scenario name:" + it.name + " id:" + it.id)
                    it.stepList.each {
                        if (it.deviceId != null) {
                            println("--- step name:" + it.name + " id:" + it.id + " category:" + it.deviceCategory + " deviceId:" + it.deviceId)
                        } else {
                            println("--- step name:" + it.name + " id:" + it.id)
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

    public void testRunFinished(){
        activeTestRun = null
    }

    public void scenarioStarted(String name){
        TestRun testrun = getInstance().activeTestRun
        if( testrun == null) {return}

        testrun.scenarioList.add(new Scenario(name))
        testrun.activeScenario = testrun.scenarioList.last()
    }

    public void scenarioFinished(){
        getInstance().activeTestRun.activeScenario = null
    }

    public void stepStarted(String name){
        Scenario scenario = getInstance().activeTestRun.activeScenario
        if(scenario == null) {return}

        scenario.stepList.add(new Step(name))
        scenario.activeStep = scenario.stepList.last()
    }

    public void stepFinished(){
        getInstance().activeTestRun.activeScenario.activeStep = null
    }

    public Step getActiveStep(){
        return getInstance().activeTestRun.activeScenario.activeStep
    }

}
