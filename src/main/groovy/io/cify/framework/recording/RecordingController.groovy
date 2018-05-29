package io.cify.framework.recording

import io.cify.framework.core.Device
import io.cify.framework.reporting.TestReportManager
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

/**
 * Created by FOB Solutions
 *
 * Responsible for recording activities
 */
class RecordingController {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('RECORDING CONTROLLER') as Marker

    private static String screenshotsReportingDir
    private static final String SCREENSHOTS_SUB_DIR = "screenshots"
    private static final String OUTPUT_MEDIA_FORMAT = ".mp4"
    private static final String OUTPUT_SCREENSHOT_FORMAT = ".png"
    private static final String TEMP = "temp"
    private static final int FPS = 2

    /**
     * Start recording
     *
     * @param device - device to record
     * */
    static void startRecording(Device device) {
        LOG.debug(MARKER, "Start recording...")
        if (TestReportManager.isReporting) {
            screenshotsReportingDir = System.getProperty("videoDir") + SCREENSHOTS_SUB_DIR
            new File(screenshotsReportingDir).mkdirs()
        } else {
            new File(getVideoDirForDevice(device) + TEMP).mkdirs()
        }

        Thread.start(device.id + "_recorder") {
            device.isRecording = true
            while (device.hasDriver() && device.isRecording) {
                try {
                    takeScreenshot(device)
                } catch (all) {
                    LOG.debug(MARKER, "Recording stopped cause: " + all.message)
                    device.isRecording = false
                }
                sleep((Long) (1000 / FPS))
            }
        }
    }

    /**
     * Stop recording
     *
     * @param device - device to stop recording
     * */
    static void stopRecording(Device device) {
        if (!TestReportManager.isReporting) {
            try {
                LOG.debug(MARKER, "Stop recording...")

                String fileName = device.id + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + OUTPUT_MEDIA_FORMAT
                String videoDir = getVideoDirForDevice(device)
                boolean success = RecordMedia.imagesToMedia(
                        getVideoDirForDevice(device) + TEMP,
                        getRecordingDuration(device),
                        videoDir,
                        fileName
                )
                if (success) {
                    device.getCapabilities().setCapability("video", videoDir + fileName)
                    deleteTemporaryImages(device)
                }
            } catch (all) {
                LOG.debug("Stop recording failed cause $all.message")
            }
        }
    }

    /**
     * Take screenshot
     *
     * @param device - device to take screenshot
     * */
    static void takeScreenshot(Device device) {
        if (device.isRecording)
            try {
                if (TestReportManager.isReporting) {
                    String scenarioId = TestReportManager.getActiveScenario()?.scenarioId
                    String deviceId = device?.getId()
                    String stepId = TestReportManager.getActiveStep()?.stepId
                    String actionId = TestReportManager.getActiveStepAction()?.actionId ?: 'no-action'
                    if (!scenarioId || !deviceId || !stepId || !actionId) {
                        return
                    }
                    String filename = System.currentTimeMillis() + "_" + scenarioId +
                            "_" + deviceId + "_" + stepId + "_" + actionId
                    File scrFile = ((TakesScreenshot) device.getDriver()).getScreenshotAs(OutputType.FILE)
                    if (scrFile.isFile()) {
                        scrFile.getParentFile().mkdirs()
                        FileUtils.copyFile(scrFile, new File(screenshotsReportingDir + "/" + scenarioId + "/" + filename + OUTPUT_SCREENSHOT_FORMAT))
                    }
                } else {
                    File scrFile = ((TakesScreenshot) device.getDriver()).getScreenshotAs(OutputType.FILE)
                    if (scrFile.isFile()) {
                        FileUtils.copyFile(scrFile, new File(getVideoDirForDevice(device) + TEMP + "/" + System.currentTimeMillis() + OUTPUT_SCREENSHOT_FORMAT))
                    }
                }
            } catch (all) {
                LOG.debug(MARKER, "Taking screenshot failed cause: " + all.message)
            }
    }

    /**
     * Get recording time
     * */
    static int getRecordingDuration(Device device) {
        try {
            File screenshotFolder = new File(getVideoDirForDevice(device) + TEMP)
            File[] screenshots = screenshotFolder.listFiles()
            long duration = screenshots.last().getName().replace(device.id, "").replace(OUTPUT_SCREENSHOT_FORMAT, "").toLong() - screenshots.first().getName().replace(device.id, "").replace(OUTPUT_SCREENSHOT_FORMAT, "").toLong()
            return TimeUnit.MILLISECONDS.toSeconds(duration)
        } catch (all) {
            LOG.debug("Failed to get video duration from screenshots cause: " + all.message)
            return 0
        }
    }

    /**
     * Gets video path
     * */
    private static String getVideoDirForDevice(Device device) {
        return System.getProperty("videoDir") +
                System.getProperty("task", "plug-and-play") +
                "/" +
                device.id +
                "/"
    }

    /**
     * Delete temporary screenshots
     * */
    private static void deleteTemporaryImages(Device device) {
        File screenshotFolder = new File(getVideoDirForDevice(device) + TEMP)
        screenshotFolder.deleteDir()
    }
}
