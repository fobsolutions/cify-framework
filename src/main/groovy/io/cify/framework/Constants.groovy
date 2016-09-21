package io.cify.framework

/**
 * Created by FOB Solutions
 *
 * This class is responsible for holding constant values
 */
class Constants {

    public static final String CAPABILITY = "capability"
    public static final String REMOTE = "remote"
    public static final String UI_TYPE = "UIType"
    public static final String CAPABILITY_ID = "capabilityId"
    public static final String PARAMETERS_PATH = "build/cify/parameters/"
    public static final String FULL_CAPABILITIES_FILE = "capabilities.json"

    /**
     * Capabilities accepted by selenium
     * */
    public static enum Capabilities {
        CHROME,
        SAFARI,
        OPERA,
        FIREFOX,
        ANDROID,
        INTERNETEXPLORER,
        IPAD,
        IPHONE,
        PHANTOMJS,
        EDGE
    }
}
