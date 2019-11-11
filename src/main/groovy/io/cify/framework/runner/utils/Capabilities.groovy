package io.cify.framework.runner.utils

import groovy.json.internal.LazyMap
import io.cify.framework.core.CifyFrameworkException
import io.cify.framework.core.DeviceCategory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * Created by FOB Solutions
 *
 * Set of capabilities
 */
class Capabilities {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CAPABILITIES') as Marker

    private static final CAPABILITY_ID = "capabilityId"

    List<LazyMap> ios = []
    List<LazyMap> android = []
    List<LazyMap> browser = []

    /**
     * Add capability to every object
     */
    void addCapabilitiesToAll(LazyMap extraCapabilities) {
        browser.each {
            it.putAll(extraCapabilities)
        }
        android.each {
            it.putAll(extraCapabilities)
        }
        ios.each {
            it.putAll(extraCapabilities)
        }
    }

    /**
     * Adds capabilities to specified device category
     * @param deviceCategory capabilities device category
     * @param capabilities capabilities to be added
     */
    void addCapabilities(String deviceCategory, LazyMap capabilities) {
        switch (deviceCategory) {
            case CapabilityParser.IOS:
                ios.add(capabilities)
                break
            case CapabilityParser.ANDROID:
                android.add(capabilities)
                break
            case CapabilityParser.BROWSER:
                browser.add(capabilities)
                break
            default:
                throw new CifyPluginException("Unknown device category")
        }
    }

    /**
     * Returns desired capabilities for selected device category
     *
     * @param category device category
     *
     * @return DesiredCapabilities
     */
    DesiredCapabilities toDesiredCapabilities(DeviceCategory category) {
        LOG.debug(MARKER, "Get desired capabilities for category $category")
        Map result = [:]
        try {
            result = capabilityList.find {
                (it.category as String).toUpperCase() == category.toString() && it.available
            }.capabilities as LazyMap
        } catch (NullPointerException ignored) {
            new CifyFrameworkException("No available capabilities for $category found")
        }
        return new DesiredCapabilities(result)
    }

    /**
     * Generate unique string from capabilities object
     */
    @Override
    String toString() {
        String browserCapsId = getCapabilityIdentifier(getBrowser())
        String androidCapsId = getCapabilityIdentifier(getAndroid())
        String iosCapsId = getCapabilityIdentifier(getIos())

        browserCapsId = browserCapsId ? "_" + browserCapsId : ""
        androidCapsId = androidCapsId ? "_" + androidCapsId : ""
        iosCapsId = iosCapsId ? "_" + iosCapsId : ""

        return browserCapsId + androidCapsId + iosCapsId
    }

    /*
     * Generates class data representation string
     */
    String toPrettyString() {
        return "[browser: $browser, android: $android, ios: $ios]"
    }

    static String toPrettyString(List<Capabilities> capabilitiesList) {
        String prettyString = "["
        capabilitiesList.forEach() {
            prettyString += "${it.toPrettyString()}, "
        }
        prettyString = "${prettyString.take(prettyString.length()-2)}]"
        return prettyString
    }

    /**
     * Gets capability identifier from LazyMap
     */
    private static String getCapabilityIdentifier(List<LazyMap> capability) {
        if (capability) {
            StringBuilder builder = new StringBuilder()
            for (LazyMap map : capability)
                builder.append(map.containsKey(CAPABILITY_ID) ? "_" + map.get(CAPABILITY_ID) : "_" + map.hashCode())
            return builder.replaceFirst("_", "")
        } else {
            return ""
        }
    }
}
