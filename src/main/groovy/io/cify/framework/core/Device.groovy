package io.cify.framework.core

import io.cify.framework.core.interfaces.IDevice
import io.cify.framework.logging.LoggingOutputStream
import io.cify.framework.recording.RecordingController
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

/**
 * Created by FOB Solutions
 *
 * This is a model class for Device
 */

class Device implements IDevice {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('DEVICE') as Marker
    private static final String REMOTE_CAPABILITY = "remote"

    private String id
    private DeviceCategory category
    private DesiredCapabilities capabilities

    private WebDriver driver

    public boolean isRecording = false
    public boolean active = false

    /**
     * Default constructor for Device
     *
     * @param id unique device id
     * @param category device category
     * @param capabilities device desired capabilities
     * */
    protected Device(String id, DeviceCategory category, DesiredCapabilities capabilities) {
        LOG.debug(MARKER, "Create new device with id $id, category $category and capabilities $capabilities")
        this.id = id
        this.category = category
        this.capabilities = capabilities
    }

    /**
     * Gets id of device
     *
     * @retun device id
     * */
    @Override
    String getId() {
        LOG.debug(MARKER, "Get device id")
        return id
    }

    /**
     * Gets category for device
     *
     * @retun device category
     * */
    @Override
    DeviceCategory getCategory() {
        LOG.debug(MARKER, "Get device category")
        return this.category
    }

    /**
     * Gets driver from Device
     *
     * @return WebDriver
     * */
    @Override
    WebDriver getDriver() {
        LOG.debug(MARKER, "Get device driver")
        return driver
    }

    /**
     * Sets desired capability parameter
     *
     * @param key
     * @param value
     * */
    @Override
    void setCapability(String key, String value) {
        LOG.debug(MARKER, "Set desire capability $key : $value")
        if (key != null && value != null) {
            capabilities.setCapability(key, value)
        }
    }

    /**
     * Gets desired capabilities for device
     *
     * @retun DesiredCapabilities
     * */
    @Override
    DesiredCapabilities getCapabilities() {
        LOG.debug(MARKER, "Get all desired capabilities " + capabilities)
        return capabilities
    }

    /**
     * Opens app on device
     * */
    @Override
    void openApp() {
        openApp("", "", "")
    }

    /**
     * Opens app on device
     *
     * @param app
     * */
    @Override
    void openApp(String app) {
        openApp(app, "", "");
    }

    /**
     * Opens app on device
     *
     * @param app
     * @param appActivity
     * @param appPackage
     *
     * @throws CifyFrameworkException  if failed to open app
     * */
    @Override
    void openApp(String app, String appActivity, String appPackage) {
        LOG.debug(MARKER, "Open app $app, $appActivity, $appPackage")
        try {
            File appFile = new File(app)
            if (appFile.isFile() && (!app.startsWith("http://") && !app.startsWith("https://")
                                                                && !app.startsWith("s3://"))) {
                String fileName = appFile.getName()
                String path = appFile.toURI().getRawPath().replace(fileName, "").replace(":", "").toLowerCase()
                app = path + fileName

                /* app path workaround works only if test executor OS and farm OS are the same type */
                String osName = System.getProperty("os.name").toLowerCase()
                if (osName.indexOf("win") >= 0 ) {
                    app = app.substring(1, 2) + ":" + app.substring(2, app.length())
                    app.replace("/", "\\")
                }
            }

            app ? setCapability("app", app) : null
            appActivity ? setCapability("appActivity", appActivity) : null
            appPackage ? setCapability("appPackage", appPackage) : null
            createDriver()

        } catch (all) {
            LOG.debug(MARKER, all.message, all)
            throw new CifyFrameworkException("Failed to open application cause $all.message")
        }
    }

    /**
     * Opens url in device browser
     *
     * @param url
     *
     * @throws CifyFrameworkException  if failed to open url
     * */
    @Override
    void openBrowser(String url) {
        LOG.debug(MARKER, "Open url $url")
        try {
            if (!validateUrl(url)) {
                throw new CifyFrameworkException("Url is not valid")
            }
            createDriver()
            getDriver().get(url)

        } catch (all) {
            LOG.debug(MARKER, all.message, all)
            throw new CifyFrameworkException("Failed to open browser cause $all.message")
        }
    }

    /**
     * Starts video recording
     * */
    @Override
    void startRecording() {
        RecordingController.startRecording(this)
    }

    /**
     * Stops video recording
     * */
    @Override
    void stopRecording() {
        isRecording = false
        RecordingController.stopRecording(this)
    }

    /**
     * Quits app or browser and stops video recording
     * */
    @Override
    void quit() {
        LOG.debug(MARKER, "Starting to quit device with category $category")
        try {
            if (isRecording) {
                RecordingController.takeScreenshot(this)
                stopRecording()
            }

            if (capabilities.getCapability(RecordingController.FLICK_VIDEO_RECORDING_CAPABILITY) == "true" && hasDriver()) {
                String farmUrl = capabilities.getCapability(REMOTE_CAPABILITY) as String
                String sessionId = ((RemoteWebDriver) getDriver()).getSessionId().toString()
                RecordingController.stopFlickRecording(this, farmUrl, sessionId)
            }

            if (hasDriver()) {
                LOG.debug(MARKER, "Quit device driver")
                getDriver().quit()
            }
        } catch (all) {
            LOG.error(MARKER, "Failed to quit device cause $all.message")
        }
    }

    /**
     * Checks if driver exists
     * */
    boolean hasDriver() {
        LOG.debug(MARKER, "Check if driver exists")
        if (getDriver() == null) {
            LOG.debug(MARKER, "No driver found")
            return false
        }

        LOG.debug(MARKER, "Driver found")
        return true
    }

    /**
     * Creates webdriver for device
     * */
    private void createDriver() {
        LOG.debug(MARKER, "Create new device driver")
        LoggingOutputStream.redirectSystemOutAndSystemErrToLogger()
        quit()
        DeviceManager.getInstance().setDeviceActive(this)
        WebDriver driver = DriverFactory.getDriver(getCapabilities())
        this.driver = driver

        if (getCapabilities().getCapability("videoRecord") == "true") {
            startRecording()
        }
    }

    /**
     * Validates url
     *
     * @param url
     *
     * @return boolean
     * */
    private static boolean validateUrl(String url) {
        !(url == null || url.isEmpty())
    }
}