package io.cify.framework

import groovy.json.JsonSlurper
import io.cify.framework.core.DeviceCategory
import io.cify.framework.core.DeviceManager
import io.cify.framework.reporting.TestReportManager

class ReportingTest extends GroovyTestCase {

    TestReportManager trm

    void setUp() {
        trm = TestReportManager.getTestReportManager()
        trm.testRunStarted("TestAccount", "runId12334", "cucumberFeatureId")
        trm.scenarioStarted("User creates a new account with iOS", "scenarioId")
        trm.stepStarted("Given user opens iOS application")
        DeviceManager.getInstance().createDevice(DeviceCategory.IOS, "TestReportManager12345")
        trm.stepActionStarted("Open application")
        trm.stepActionFinished()
    }

    void testGetTestReportManager() {
        assert trm
    }

    void testTestRunStarted() {
        assert trm.activeTestRun
    }

    void testScenarioStarted() {
        assert trm.getActiveScenario()
    }

    void testStepStarted() {
        assert trm.getActiveStep()
    }

    void testStepActionStarted() {
        assert trm.getActiveStepAction()
    }

    void testAddDeviceToTestReport() {
        assert trm.getActiveScenario().deviceList
        assert trm.getActiveScenario().deviceList.first().get("deviceId") == "TestReportManager12345"
        assert trm.getActiveScenario().deviceList.first().get("deviceCategory") == "IOS"
    }

    void tearDown() {

        assert new JsonSlurper().parseText(trm.stepFinished("passed", 17345, null))
        assert new JsonSlurper().parseText(trm.scenarioFinished("passed", null))
        assert new JsonSlurper().parseText(trm.testRunFinished())
        DeviceManager.getInstance().quitAllDevices()

        assert !trm.getActiveStepAction()
        assert !trm.getActiveStep()
        assert !trm.getActiveScenario()
        assert !trm.getActiveScenario()
        assert !trm.activeTestRun
    }
}

