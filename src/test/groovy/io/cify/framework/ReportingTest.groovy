package io.cify.framework

import io.cify.framework.core.DeviceCategory
import io.cify.framework.core.DeviceManager
import io.cify.framework.reporting.TestReportManager

import java.text.DecimalFormat

class ReportingTest extends GroovyTestCase {

    TestReportManager trm

    void testFlow() {
        trm = TestReportManager.getTestRunManager()

        trm.testRunStarted("TestAccount","runId12334")
        trm.scenarioStarted("User creates a new account with iOS")
        trm.stepStarted("Given user opens iOS application")

        DeviceManager.getInstance().createDevice(DeviceCategory.IOS, "iOSDeviceId")
        DeviceManager.getInstance().createDevice(DeviceCategory.ANDROID, "androidId")

        trm.stepFinished("pass1",3104567345,null)
        trm.stepStarted("When user creates a new account with random credentials")
        trm.stepFinished("pass2",2203456789,null)
        trm.stepStarted("Then new user account is created")
        trm.stepFinished("pass3",5303456123,null)
        trm.scenarioFinished("pass4", null)
        trm.testRunFinished()

    }

    void testDates(){
        long nanos = 1000000000
        long millis = 1000
        DecimalFormat df = new DecimalFormat("#.00");
        long duration = 2405324597
        println("duration in seconds: " + df.format(duration/nanos))

        long start = System.currentTimeMillis()
        Thread.sleep(1750)
        long end = System.currentTimeMillis()
        duration = end - start
        println("duration in seconds: " + df.format(duration/millis))
        println("start: "+ new Date(start))
        println("end: "+ new Date(end))


    }

}
