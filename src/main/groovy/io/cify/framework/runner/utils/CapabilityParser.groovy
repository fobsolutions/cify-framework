package io.cify.framework.runner.utils

import groovy.json.JsonSlurper
import groovy.json.internal.LazyMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger

/**
 * This class is responsible for handling capabilities
 *
 * Created by FOB Solutions
 */
class CapabilityParser {
    /**
     * Capabilities parsing strategy, to put all capabilities into one {@link Capabilities} object
     */
    public static final String STRATEGY_ALL_IN_ONE = "all_in_one"
    /**
     * Capabilities parsing strategy, to put each capability into a separate {@link Capabilities} object
     */
    public static final String STRATEGY_ONE_BY_ONE = "one_by_one"
    /**
     * Capabilities parsing strategy, that creates all possible variations of capabilities with one capability
     * of each kind (browser, android, ios) per {@link Capabilities} object
     */
    public static final String STRATEGY_VARIATIONS = "variations"

    public static final String ANDROID = "android"
    public static final String IOS = "ios"
    public static final String BROWSER = "browser"

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger

    private static final String CAPABILITIES = "capabilities"
    private static final String STRATEGY = "strategy"

    /**
     * Gets full capabilities list from a file
     *
     * @return list of capabilities
     */
    static List<Capabilities> generateCapabilitiesList(String capabilitiesFilePath, String farmUrl, String capabilitiesFromCmd) {
        LazyMap capabilitiesContent

        if (!capabilitiesFromCmd.isEmpty()) {
            capabilitiesContent = new JsonSlurper().parseText(capabilitiesFromCmd) as LazyMap
        } else {
            capabilitiesContent = getCapabilitiesFromFile(capabilitiesFilePath)
        }

        LOG.debug("capabilitiesContent: $capabilitiesContent")
        List capabilities = getCapabilities(capabilitiesContent, capabilitiesContent.get(STRATEGY) as String)
        LazyMap extraCapabilitiesMap = new LazyMap()

        if (!farmUrl.isEmpty()) {
            extraCapabilitiesMap.put("remote", farmUrl)
        }

        List capabilitiesList = getCapabilitiesList(capabilities, extraCapabilitiesMap)

        LOG.debug("Parsed capabilities list is: ${Capabilities.toPrettyString(capabilitiesList)}")
        return capabilitiesList
    }

    /**
     * Gets {@link Capabilities} based on {@link String} strategy specified
     *
     * @param capabilitiesFileContent capabilities file content
     * @param strategy parsing strategy
     * @return the list of {@link Capabilities}
     */
    private static List getCapabilities(LazyMap capabilitiesFileContent, String strategy) {
        LOG.debug("Parsing capabilities with strategy: " + strategy)
        switch (strategy) {
            case STRATEGY_VARIATIONS:
                return getCapabilitiesVariations(capabilitiesFileContent)
            case STRATEGY_ONE_BY_ONE:
                return getCapabilitiesOneByOne(capabilitiesFileContent)
            case STRATEGY_ALL_IN_ONE:
                return getCapabilitiesAllInOne(capabilitiesFileContent)
            default:
                return getCapabilitiesVariations(capabilitiesFileContent)
        }
    }

    /**
     * Gets all variations of capabilities from capabilities file content
     *
     * @param capabilitiesFileContent - capabilities file content
     * @return the list of {@link Capabilities}
     */
    private static List getCapabilitiesVariations(LazyMap capabilitiesFileContent) {
        Map<String, List> capabilitiesMap = capabilitiesFileContent[CAPABILITIES] as Map<String, List> ?: new HashMap<String, List>()
        //Remove empty device categories
        def iterator = capabilitiesMap.iterator()
        while (iterator.hasNext()) {
            List list = iterator.next().value
            if (!list || list.size() == 0) {
                iterator.remove()
            }
        }

        Set<String> deviceCategories = capabilitiesMap.keySet()
        List capabilities = []

        switch (deviceCategories.size()) {
            case 3:
                for (int i = 0; i < capabilitiesMap[IOS].size(); i++) {
                    LazyMap ios = capabilitiesMap[IOS].get(i) as LazyMap
                    for (int a = 0; a < capabilitiesMap[ANDROID].size(); a++) {
                        LazyMap android = capabilitiesMap[ANDROID].get(a) as LazyMap
                        for (int b = 0; b < capabilitiesMap[BROWSER].size(); b++) {
                            LazyMap browser = capabilitiesMap[BROWSER].get(b) as LazyMap
                            Capabilities capability = new Capabilities()
                            capability.addCapabilities(IOS, ios)
                            capability.addCapabilities(ANDROID, android)
                            capability.getBrowser().add(browser)
                            capabilities.add(capability)
                        }
                    }
                }
                break
            case 2:
                String deviceCategory1 = deviceCategories[0]
                String deviceCategory2 = deviceCategories[1]
                for (int i = 0; i < capabilitiesMap[deviceCategory1].size(); i++) {
                    LazyMap capabilities1 = capabilitiesMap[deviceCategory1].get(i) as LazyMap
                    for (int j = 0; j < capabilitiesMap[deviceCategory2].size(); j++) {
                        LazyMap capabilities2 = capabilitiesMap[deviceCategory2].get(j) as LazyMap
                        Capabilities capability = new Capabilities()
                        capability.addCapabilities(deviceCategory1, capabilities1)
                        capability.addCapabilities(deviceCategory2, capabilities2)
                        capabilities.add(capability)
                    }
                }
                break
            case 1:
                String deviceCategory = deviceCategories[0]
                for (int i = 0; i < capabilitiesMap[deviceCategory].size(); i++) {
                    Capabilities capability = new Capabilities()
                    capability.addCapabilities(deviceCategory, capabilitiesMap[deviceCategory].get(i) as LazyMap)
                    capabilities.add(capability)
                }
                break
        }
        return capabilities
    }

    /**
     * Gets capabilities from capabilities file content and puts each capability into a separate {@link Capabilities} object
     *
     * @param capabilitiesFileContent - capabilities file content
     * @return the list of {@link Capabilities}
     */
    private static List getCapabilitiesOneByOne(LazyMap capabilitiesFileContent) {
        List iOSCapabilitiesList = getCapabilitiesForType(capabilitiesFileContent, IOS)
        List browserCapabilitiesList = getCapabilitiesForType(capabilitiesFileContent, BROWSER)
        List androidCapabilitiesList = getCapabilitiesForType(capabilitiesFileContent, ANDROID)

        List capabilities = []

        iOSCapabilitiesList.each {
            Capabilities capability = new Capabilities()
            capability.getIos().add(it as LazyMap)
            capabilities.add(capability)
        }

        androidCapabilitiesList.each {
            Capabilities capability = new Capabilities()
            capability.getAndroid().add(it as LazyMap)
            capabilities.add(capability)
        }

        browserCapabilitiesList.each {
            Capabilities capability = new Capabilities()
            capability.getBrowser().add(it as LazyMap)
            capabilities.add(capability)
        }

        return capabilities
    }

    /**
     * Gets capabilities from capabilities file content and puts them all to one {@link Capabilities} object
     *
     * @param capabilitiesFileContent - capabilities file content
     * @return the list of {@link Capabilities}
     */
    private static List getCapabilitiesAllInOne(LazyMap capabilitiesFileContent) {
        List capabilities = []
        if (capabilitiesFileContent != null && !capabilitiesFileContent.isEmpty()) {
            capabilities.add(capabilitiesFileContent.get(CAPABILITIES) as Capabilities)
        }
        return capabilities
    }

    /**
     * Gets capability for device type
     *
     * @param capabilityFileContent - Capabilities json file content
     * @type - device type
     */
    private static List getCapabilitiesForType(LazyMap capabilityFileContent, String type) {
        def capabilities
        if (capabilityFileContent != null && !capabilityFileContent.isEmpty()) {
            capabilities = capabilityFileContent.get(CAPABILITIES)
        }
        if (capabilities != null && capabilities[type] != null) {
            return capabilities[type] as List
        } else {
            return []
        }
    }

    /**
     * Validates capabilities structure and gets capabilities
     *
     * @param capabilities - capability file content
     * @param extraCapabilities - parameters to add to every capability
     * @return list of capabilities
     */
    private static List getCapabilitiesList(List capabilities, LazyMap extraCapabilities) {
        List capabilitiesSet
        try {
            capabilitiesSet = capabilities != null ? capabilities : []
            capabilitiesSet.each { capabilitiesObject ->
                (capabilitiesObject as Capabilities).addCapabilitiesToAll(extraCapabilities)
            }
        } catch (all) {
            LOG.debug(all.message, all)
            throw new CifyPluginException("Cannot get capabilitiesList from map cause: " + all.message)
        }
        return capabilitiesSet
    }

    /**
     * Gets capability file content as a map
     *
     * @param capabilitiesFilePath the path to a file
     * @return {@link LazyMap} of json file content
     */
    private static LazyMap getCapabilitiesFromFile(String capabilitiesFilePath) {
        LazyMap capabilitiesMap = new LazyMap()
        try {
            if (capabilitiesFilePath.startsWith("http://")
                    || capabilitiesFilePath.startsWith("https://")) {
                LOG.info("Reading capability content from URL")
                capabilitiesMap = readFromContent(capabilitiesFilePath.toURL().text) as LazyMap
            } else {
                LOG.info("Reading capability content from File")
                File capabilitiesFile = new File(capabilitiesFilePath)
                if (capabilitiesFile.exists()) {
                    LOG.debug("Found capabilities file with name: " + capabilitiesFile.getName())
                    capabilitiesMap = readFromContent(capabilitiesFile.text) as LazyMap
                } else {
                    LOG.debug("Cannot find capabilities file")
                }
            }
        } catch (all) {
            LOG.debug(all.message, all)
            throw new CifyPluginException("Parsing capabilities file content failed with message: " + all.message)
        }
        return capabilitiesMap
    }

    /**
     * Reads content from capabilities file
     *
     * @param text - capabilities file content in string format
     * @return LazyMap with capabilities
     */
    private static Object readFromContent(String text) {
        try {
            JsonSlurper jsonParser = new JsonSlurper()
            def content = jsonParser.parseText(text)
            LOG.info("Read capability file with content: " + content)
            return content
        } catch (all) {
            LOG.debug(all.message, all)
            throw new CifyPluginException("Cannot read capability file content cause: " + all.message)
        }
    }
}
