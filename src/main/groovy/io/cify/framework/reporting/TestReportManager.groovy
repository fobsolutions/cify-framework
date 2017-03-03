package io.cify.framework.reporting

import groovy.json.internal.LazyMap
import io.cify.framework.core.DeviceCategory
import io.cify.framework.core.DeviceManager

import static java.util.UUID.randomUUID

/**
 * Created by FOB Solutions
 *
 * This class is responsible for test report managing
 */
class TestReportManager {

    public static testsuiteId
    public static TestRun activeTestRun
    private static volatile TestReportManager instance
    public static boolean isReporting = false

    public TestReportManager() {
        isReporting = true
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
     * @param testsuiteId
     */
    public static void testRunStarted(String name, String testsuiteId, String cucumberTestrunId) {
        getInstance().testsuiteId = testsuiteId
        activeTestRun = new TestRun(name, cucumberTestrunId)
    }

    /**
     * Creating new Scenario object when test scenario is started
     * @param name
     */
    public static void scenarioStarted(String name, String cucumberScenarioId) {
        activeTestRun?.with {
            scenarioList.add(new Scenario(name, cucumberScenarioId))
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
     * Adding information about finished test run and returns json report
     * @return String
     */
    public static String testRunFinished() {
        String report = null
        activeTestRun?.with {
            endDate = System.currentTimeMillis()
            duration = activeTestRun.endDate - activeTestRun.startDate
            result = isTestRunFailed() ? "failed" : "passed"
            report = Report.reportTestRun(it)
            activeTestRun = null
        }
        return report
    }

    /**
     * Returning true if current active test run is failed
     * @return boolean
     */
    private static boolean isTestRunFailed() {
        return activeTestRun?.scenarioList?.find { it.result == "failed" }
    }

    /**
     * Adding information about finished test scenario and returns json report
     * @param result
     * @param errorMessage
     * @return String
     */
    public static String scenarioFinished(String result, String errorMessage) {
        getActiveScenario()?.with {
            endDate = System.currentTimeMillis()
            duration = endDate - startDate
            it.errorMessage = errorMessage
            it.result = isScenarioFailed() ? "failed" : result
            return Report.reportScenario(it)
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
     * Adding information about finished test step and returns json report
     * @param result
     * @param duration
     * @param errorMessage
     * @return String
     */
    public static String stepFinished(String result, long duration, String errorMessage) {
        String scenarioId = getActiveScenario()?.scenarioId
        LazyMap map = getActiveScenario()?.deviceList?.last()
        getActiveStep()?.with {
            if(map) {
                it.device.putAll(map)
            }
            it.errorMessage = errorMessage
            it.duration = duration
            endDate = System.currentTimeMillis()
            startDate = endDate - duration
            it.result = result
            return Report.reportStep(it, scenarioId)
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
        LazyMap device = [:]
        device.put("deviceId", deviceId)
        device.put("deviceCategory", deviceCategory)
        device.putAll(DeviceManager.getInstance().getCapabilities().toDesiredCapabilities(DeviceCategory.valueOf(deviceCategory)).asMap())
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
        if (getActiveStep()?.stepActionsList?.size() > 0) {
            return getActiveStep().stepActionsList.last()
        } else {
            return null
        }
    }

    /**
     * Generates unique id
     * @return string
     */
    public static String generateId() {
        return (System.currentTimeMillis() + "-" + randomUUID()) as String
    }
}
