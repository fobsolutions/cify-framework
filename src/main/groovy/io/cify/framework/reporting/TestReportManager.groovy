package io.cify.framework.reporting

import static java.util.UUID.randomUUID

/**
 * Created by FOB Solutions
 *
 * This class is responsible for test report managing
 */
class TestReportManager {

    public static testSuiteId
    public static TestRun activeTestRun
    private static volatile TestReportManager instance

    public TestReportManager() {
    }

    private static TestReportManager getInstance() {
        if (instance == null) {
            synchronized (TestReportManager.class) {
                if (instance == null) {
                    instance = new TestReportManager()
                }
            }
        }
        return instance
    }

    /**
     * Returning instance of TestReportManager class object
     * @return TestReportManager
     */
    public static TestReportManager getTestReportManager() {
        return getInstance()
    }

    /**
     * Creating new TestRun object when test run is started
     * @param name
     * @param testSuiteId
     */
    public static void testRunStarted(String name, String testSuiteId) {
        getInstance().testSuiteId = testSuiteId
        activeTestRun = new TestRun(name)
    }

    /**
     * Creating new Scenario object when test scenario is started
     * @param name
     */
    public static void scenarioStarted(String name) {
        activeTestRun?.with {
            scenarioList.add(new Scenario(name))
            activeScenario = scenarioList.last()
        }
    }

    /**
     * Creating new Step object when test step is started
     * @param name
     */
    public static void stepStarted(String name) {
        getActiveScenario()?.stepList?.add(new Step(name))
    }

    /**
     * Creating new StepAction object when test step action is started
     * @param name
     */
    public static void stepActionStarted(String name) {
        getActiveStep()?.stepActionsList?.add(new StepAction(name))
    }

    /**
     * Adding and report information about finished test run
     */
    public static void testRunFinished() {
        activeTestRun?.with {
            endDate = System.currentTimeMillis()
            duration = activeTestRun.endDate - activeTestRun.startDate
            result = isTestRunFailed() ? "failed" : "passed"
            Report.reportTestRun(it)
            activeTestRun = null
        }
    }

    /**
     * Returning true if current active test run is failed
     * @return boolean
     */
    private static boolean isTestRunFailed() {
        return activeTestRun?.scenarioList?.find { it.result == "failed" }
    }

    /**
     * Adding and report information about finished test scenario
     * @param result
     * @param errorMessage
     */
    public static void scenarioFinished(String result, String errorMessage) {
        getActiveScenario()?.with {
            endDate = System.currentTimeMillis()
            duration = endDate - startDate
            it.errorMessage = errorMessage
            it.result = isScenarioFailed() ? "failed" : result
            Report.reportScenario(it)
        }
    }

    /**
     * Returning true if current active scenario is failed
     * @return boolean
     */
    private static boolean isScenarioFailed() {
        return getActiveScenario()?.stepList?.find { it.result == "failed" }
    }

    /**
     * Adding and report information about finished test step
     * @param result
     * @param duration
     * @param errorMessage
     */
    public static void stepFinished(String result, long duration, String errorMessage) {
        String scenarioId = getActiveScenario()?.scenarioId
        getActiveStep()?.with {
            it.errorMessage = errorMessage
            it.duration = duration
            it.result = result
            Report.reportStep(it, scenarioId)
        }
    }

    /**
     * Adding information about finished test step action
     */
    public static void stepActionFinished() {
        getActiveStepAction()?.with {
            endDate = System.currentTimeMillis()
            duration = endDate - startDate
        }
    }

    /**
     * Adding device information to current active scenario
     * @param deviceId
     * @param deviceCategory
     */
    public static void addDeviceToTestReport(String deviceId, String deviceCategory) {
        Map<String, String> device = new HashMap<>()
        device.put("deviceId", deviceId)
        device.put("deviceCategory", deviceCategory)
        getActiveScenario()?.deviceList?.add(device)
    }

    /**
     * Returns current active scenario
     * @return Scenario object
     */
    public static Scenario getActiveScenario() {
        return activeTestRun?.activeScenario
    }

    /**
     * Returns current active step
     * @return Step object
     */
    public static Step getActiveStep() {
        return getActiveScenario()?.stepList?.find { !it.result }
    }

    /**
     * Returns current active step action
     * @return StepAction object
     */
    public static StepAction getActiveStepAction() {
        return getActiveStep()?.stepActionsList?.last()
    }

    /**
     * Generates unique id
     * @return string
     */
    public static String generateId() {
        return (System.currentTimeMillis() + "-" + randomUUID()) as String
    }
}
