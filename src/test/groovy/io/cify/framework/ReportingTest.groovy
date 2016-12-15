package io.cify.framework

import io.cify.framework.core.DeviceCategory
import io.cify.framework.core.DeviceManager
import io.cify.framework.reporting.TestRunManager

class ReportingTest extends GroovyTestCase {

    TestRunManager trm

    void testFlow() {
        trm = TestRunManager.getInstance()
        trm.testSuiteStarted()

        trm.testRunStarted("TestAccount")
        trm.scenarioStarted("User creates a new account with iOS")
        trm.stepStarted("Given user opens iOS application")

        DeviceManager.getInstance().createDevice(DeviceCategory.IOS, "iOSDeviceId")

        trm.stepFinished("pass1",110456,null)
        trm.stepStarted("When user creates a new account with random credentials")
        trm.stepFinished("pass2",1203456,null)
        trm.stepStarted("Then new user account is created")
        trm.stepFinished("pass3",1303456,null)
        trm.scenarioFinished("pass4",5412345,null)
        trm.testRunFinished("pass5")

        trm.testSuiteFinished()
    }

}
