package io.cify.framework.recording

import io.cify.framework.core.Device
import io.cify.framework.http.HttpConnector
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
    private static final String TEMP = "-temporary"
    private static final int FPS = 2
    static final String FLICK_VIDEO_RECORDING_CAPABILITY = "flickRecording"

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
            new File(getVideoDirForDevice(device)).mkdirs()
            new File(getOutputVideoDirForDevice(device)).mkdirs()
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

                String fileName = getOutputVideoFilename(device)
                boolean success = RecordMedia.imagesToMedia(
                        getVideoDirForDevice(device),
                        getRecordingDuration(device),
                        getOutputVideoDirForDevice(device),
                        fileName
                )
                if (success) {
                    String taskName = System.getProperty("task", "plug-and-play") + "/"
                    device.getCapabilities().setCapability("video", taskName + fileName)
                    deleteTemporaryImages(device)
                }
            } catch (all) {
                LOG.debug("Stop recording failed cause $all.message")
            }
        }
    }

    /**
     * Stop flick video recording
     *
     * @param device - device to stop flick recording
     * @param farmUrl - device farm url
     * @param sessionId - device session id
     * */
    public static void stopFlickRecording(Device device, String farmUrl, String sessionId) {
        try {
            LOG.debug(MARKER, "Stop flick recording...")
            String outputFile = getOutputVideoDirForDevice(device) + getOutputVideoFilename(device)
            LOG.debug(MARKER, "Parameters: url=$farmUrl, sessionId=$sessionId, outputFile=$outputFile")

            if (farmUrl && sessionId && outputFile) {
                HttpConnector.requestDeviceStopRecording(farmUrl, sessionId, outputFile)
            } else {
                LOG.debug(MARKER, "Missing or wrong parameter")
            }

        } catch (all) {
            LOG.debug("Stop flick recording failed cause $all.message")
        }
    }

    /**
     * Take screenshot
     *
     * @param device - device to take screenshot
     * */
    static void takeScreenshot(Device device) {
        if (device.isRecording) {
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
                        FileUtils.copyFile(scrFile, new File(getVideoDirForDevice(device) + "/" + System.currentTimeMillis() + OUTPUT_SCREENSHOT_FORMAT))
                    }
                }
            } catch (all) {
                LOG.debug(MARKER, "Taking screenshot failed cause: " + all.message)
            }
        }
    }

    /**
     * Get recording time
     * */
    static int getRecordingDuration(Device device) {
        try {
            File screenshotFolder = new File(getVideoDirForDevice(device))
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
                "/" + device.id + TEMP
    }

    /**
     * Gets output video path
     * */
    private static String getOutputVideoDirForDevice(Device device) {

        String taskName = System.getProperty("task")
        String videoDir = System.getProperty("videoDir")

        if (!videoDir) {
            videoDir = "build/cify/videos/"
        }

        return taskName ? videoDir + taskName + "/" :
                videoDir + "plug-and-play" + "/" + device.id + "/"
    }

    /**
     * Gets output video file name
     * */
    private static String getOutputVideoFilename(Device device) {
        return new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + device.id + OUTPUT_MEDIA_FORMAT
    }

    /**
     * Delete temporary screenshots
     * */
    private static void deleteTemporaryImages(Device device) {
        File screenshotFolder = new File(getVideoDirForDevice(device))
        screenshotFolder.deleteDir()
    }
}
