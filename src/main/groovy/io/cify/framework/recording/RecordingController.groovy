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
 */
class RecordingController {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('RECORDING') as Marker

    /**
     * Start recording
     *
     * @param device - device to record
     * */
    public static void startRecording(Device device) {
        LOG.debug(MARKER, "Start recording...")
        Thread.start(device.id + "_recorder") {
            device.isRecording = true
            if (new File(getVideoPathForDevice(device) + "temp/").mkdirs()) {
                while (device.hasDriver() && device.isRecording) {
                    try {
                        File scrFile = ((TakesScreenshot) device.getDriver()).getScreenshotAs(OutputType.FILE)
                        FileUtils.copyFile(scrFile, new File(getVideoPathForDevice(device) + "temp/" + device.id + System.currentTimeMillis() + ".png"))
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
            LOG.debug(MARKER, "Video duration was " + getRecordingDuration(device) + " for device " + device.id)
            device.isRecording = false

            Recording.screenshotsToVideo(
                    getVideoPathForDevice(device) + "temp",
                    getRecordingDuration(device),
                    getVideoPathForDevice(device),
                    device.id + new Date().time + ".mp4"
            )

            deleteTemporaryImages(device)
        } catch (ignored) {
        }
    }

    /**
     * Get recording time
     * */
    public static int getRecordingDuration(Device device) {
        File screenshotFolder = new File(getVideoPathForDevice(device) + "temp")
        File[] screenshots = screenshotFolder.listFiles()
        long duration = screenshots.last().getName().replace(device.id, "").replace(".png", "").toLong() - screenshots.first().getName().replace(device.id, "").replace(".png", "").toLong()
        return TimeUnit.MILLISECONDS.toSeconds(duration)
    }

    /**
     * Gets video path
     * */
    private static String getVideoPathForDevice(Device device) {
        return System.getProperty("videoPath", "build/cify/videos/") +
                System.getProperty("task", "plug-and-play") +
                "/" +
                device.id +
                "/";
    }

    /**
     * Delete temporary screenshots
     * */
    private static void deleteTemporaryImages(Device device) {
        File screenshotFolder = new File(getVideoPathForDevice(device) + "temp")
        screenshotFolder.deleteDir()
    }
}
