package io.cify.framework

import io.cify.framework.core.Device
import io.cify.framework.core.DeviceCategory
import io.cify.framework.reporting.TestRunManager
import org.openqa.selenium.remote.DesiredCapabilities

class ReportingTest extends GroovyTestCase {

    TestRunManager trm

    void testFlow(){
        trm = TestRunManager.getInstance()
        trm.testSuiteStarted()

        trm.testRunStarted("TestAccount")
        trm.scenarioStarted("User creates a new account with iOS")
        trm.stepStarted("Given user opens iOS application")

        DesiredCapabilities iOSCaps = new DesiredCapabilities(["version": "9.3.5"])
        Device iOSDevice = new Device("iOSDeviceId", DeviceCategory.IOS, iOSCaps)
        trm.getActiveStep().deviceId = iOSDevice.getId()
        trm.getActiveStep().deviceCategory = iOSDevice.getCategory()

        trm.stepFinished()
        trm.stepStarted("When user creates a new account with random credentials")
        trm.stepFinished()
        trm.stepStarted("Then new user account is created")
        trm.stepFinished()
        trm.scenarioFinished()
        trm.testRunFinished()

        trm.testSuiteFinished()
    }

}
