package io.cify.framework

import io.appium.java_client.pagefactory.AppiumFieldDecorator
import io.cify.framework.core.Device
import io.cify.framework.core.DeviceManager
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.PageFactoryFinder

import java.util.concurrent.TimeUnit

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
     * @param deviceId - Device id
     * */
    PageObjects(String deviceId) {
        this(deviceId, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    /**
     * Initializes page object for specific device id and timeout
     *
     * @param deviceId - Device id
     * @param timeOutInSeconds - timeout time in seconds
     * */
    PageObjects(String deviceId, long timeOutInSeconds) {
        this(DeviceManager.getInstance().getActiveDevice(deviceId), timeOutInSeconds)
    }

    /**
     * Initializes page object for specific device
     *
     * @param device - Device object
     * */
    PageObjects(Device device) {
        this(device, DEFAULT_TIMEOUT_IN_SECONDS)
    }

    /**
     * Initializes page object for specific device and timeout
     *
     * @param device - Device object
     * @param timeOutInSeconds - timeout
     * */
    PageObjects(Device device, long timeOutInSeconds) {
        initElements(device, timeOutInSeconds)
    }

    /**
     * PageFactory initializes page object for device with timeout
     *
     * @param device - Device object
     * @param timeOutInSeconds - timeout
     * */
    private void initElements(Device device, long timeOutInSeconds) {
        PageFactory.initElements(new AppiumFieldDecorator(device.getDriver(), timeOutInSeconds, TimeUnit.SECONDS), this)
    }
}
