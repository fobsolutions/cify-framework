package io.cify.framework.recording

import io.cify.framework.core.Device
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot

import java.util.concurrent.TimeUnit

/**
 * Created by FOB Solutions
 *
 * Responsible for recording activities
 */
class RecordingController {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('RECORDING CONTROLLER') as Marker

    private static final String OUTPUT_MEDIA_FORMAT = ".mp4"
    private static final String OUTPUT_SCREENSHOT_FORMAT = ".png"
    private static final String TEMP = "temp"

    /**
     * Start recording
     *
     * @param device - device to record
     * */
    public static void startRecording(Device device) {
        LOG.debug(MARKER, "Start recording...")
        Thread.start(device.id + "_recorder") {
            device.isRecording = true
            if (new File(getVideoDirForDevice(device) + TEMP).mkdirs()) {
                while (device.hasDriver() && device.isRecording) {
                    try {
                        takeScreenshot(device)
                    } catch (all) {
                        LOG.debug(MARKER, "Recording stopped cause: " + all.message)
                        device.isRecording = false
                    }
                }
            }
        }
    }

    /**
     * Stop recording
     *
     * @param device - device to stop recording
     * */
    public static void stopRecording(Device device) {
        try {
            LOG.debug(MARKER, "Stop recording...")
            device.isRecording = false

            Recording.imagesToMedia(
                    getVideoDirForDevice(device) + TEMP,
                    getRecordingDuration(device),
                    getVideoDirForDevice(device),
                    device.id + new Date().time + OUTPUT_MEDIA_FORMAT
            )

            deleteTemporaryImages(device)
        } catch (ignored) {
        }
    }

    /**
     * Take screenshot
     *
     * @param device - device to take screenshot
     * */
    public static void takeScreenshot(Device device) {
        try {
            File scrFile = ((TakesScreenshot) device.getDriver()).getScreenshotAs(OutputType.FILE)
            FileUtils.copyFile(scrFile, new File(getVideoDirForDevice(device) + TEMP + device.id + System.currentTimeMillis() + OUTPUT_SCREENSHOT_FORMAT))
        } catch (all) {
            LOG.debug(MARKER, "Taking screenshot failed cause: " + all.message)
        }
    }

    /**
     * Get recording time
     * */
    public static int getRecordingDuration(Device device) {
        File screenshotFolder = new File(getVideoDirForDevice(device) + TEMP)
        File[] screenshots = screenshotFolder.listFiles()
        long duration = screenshots.last().getName().replace(device.id, "").replace(OUTPUT_SCREENSHOT_FORMAT, "").toLong() - screenshots.first().getName().replace(device.id, "").replace(OUTPUT_SCREENSHOT_FORMAT, "").toLong()
        return TimeUnit.MILLISECONDS.toSeconds(duration)
    }

    /**
     * Gets video path
     * */
    private static String getVideoDirForDevice(Device device) {
        return System.getProperty("videoDir", "build/cify/videos/") +
                System.getProperty("task", "plug-and-play") +
                "/" +
                device.id +
                "/";
    }

    /**
     * Delete temporary screenshots
     * */
    private static void deleteTemporaryImages(Device device) {
        File screenshotFolder = new File(getVideoDirForDevice(device) + TEMP)
        screenshotFolder.deleteDir()
    }
}
