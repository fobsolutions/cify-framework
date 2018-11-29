package io.cify.framework

import io.appium.java_client.pagefactory.AppiumFieldDecorator
import io.cify.framework.core.DeviceManager
import io.cify.framework.core.WebDriverDevice
import org.openqa.selenium.support.PageFactory

import java.time.Duration

/**
 * Page object class
 * */
class PageObjects {

    /**
     * Default timeout for page object
     * */
    private static final long DEFAULT_TIMEOUT_IN_SECONDS = 10

    /**
     * Initializes page object for specific device id
     *
     * @param deviceId - WebDriverDevice id
     * */
    PageObjects(String deviceId) {
        this(deviceId, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    /**
     * Initializes page object for specific device id and timeout
     *
     * @param deviceId - WebDriverDevice id
     * @param timeOutInSeconds - timeout time in seconds
     * */
    PageObjects(String deviceId, long timeOutInSeconds) {
        this(DeviceManager.getInstance().getActiveDevice(deviceId), timeOutInSeconds)
    }

    /**
     * Initializes page object for specific device
     *
     * @param device - WebDriverDevice object
     * */
    PageObjects(WebDriverDevice device) {
        this(device, DEFAULT_TIMEOUT_IN_SECONDS)
    }

    /**
     * Initializes page object for specific device and timeout
     *
     * @param device - WebDriverDevice object
     * @param timeOutInSeconds - timeout
     * */
    PageObjects(WebDriverDevice device, long timeOutInSeconds) {
        initElements(device, timeOutInSeconds)
    }

    /**
     * PageFactory initializes page object for device with timeout
     *
     * @param device - WebDriverDevice object
     * @param timeOutInSeconds - timeout
     * */
    private void initElements(WebDriverDevice device, long timeOutInSeconds) {
        PageFactory.initElements(new AppiumFieldDecorator(device.getDriver(), Duration.ofSeconds(timeOutInSeconds)), this)
    }
}
