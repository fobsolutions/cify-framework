package io.cify.framework.runner.utils

import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.apache.commons.validator.routines.UrlValidator
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.gradle.api.Project

/**
 * This class is responsible for managing plugin parameters
 *
 * Created by FOB Solutions
 */

class PluginExtensionManager {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('EXTENSION MANAGER') as Marker

    Project project
    Properties envProperties = new Properties()

    PluginExtensionManager(Project p) {
        LOG.debug(MARKER, "Create new PluginExtensionsManager")
        this.project = p
    }

    /**
     * Setup method for creating and updating extension file
     */
    void setupParameters() {
        LOG.debug(MARKER, "Setup parameters")

        setEnvProperties()

        setThreads()
        setGlue()
        setTags()
        setPlugins()
        addPlugins()
        setDryRun()
        setStrict()
        setMonochrome()
        setIgnoreFailures()
        setFeatureDirs()
        setCapabilitiesFilePath()
        setFarmUrl()
        setCommandLineCapabilities()
        setCredentials()
        setRerunFailedTests()
        setRepeat()

        setCapabilities()
        setFeatures()

        setVideoRecord()
        setVideoDir()

        setAuthService()
        setProjectName()
        setSuiteName()
        setAccessKey()
    }

    /**
     * Sets envProperties from property file
     * */
    void setEnvProperties() {
        LOG.debug(MARKER, "Set env properties")

        if (!project.hasProperty('env')) {
            LOG.debug(MARKER, "No env parameter provided")
            return
        }

        String fileName = project.env + ".properties"
        LOG.debug(MARKER, "Using $fileName")

        File propertiesFile = getFile(fileName)

        envProperties.load(readPropertiesFromFile(propertiesFile))
    }

    /**
     * Checks if file have given property
     *
     * @param paramName parameter name from file
     * @return if property is selected in file
     * */
    boolean hasEnvProperty(String paramName) {
        try {
            !envProperties.isEmpty() && !envProperties.getProperty(paramName).isEmpty()
        } catch (ignored) {
            false
        }
    }

    /**
     * Gets envProperties file for root path
     *
     * @return envProperties file
     * */
    static File getFile(String fileName) {
        File propertiesFile = new File(fileName)

        if (!propertiesFile.exists()) {
            throw new FileNotFoundException("Cannot find file with name $fileName")
        }
        propertiesFile
    }

    /**
     * Reads envProperties from the file
     *
     * @param envProperties file
     * */
    static InputStream readPropertiesFromFile(File propertiesFile) {
        try {
            new ByteArrayInputStream(propertiesFile.getBytes())
        } catch (all) {
            throw new CifyPluginException("Cannot read input stream from env properties file $propertiesFile", all)
        }
    }

    /**
     * Searches value from different locations and returns value with highest priority
     *
     * @return parameter value
     * */
    String getValue(String paramName) {
        LOG.debug(MARKER, "Get value for parameter $paramName")

        String content
        if (project.hasProperty(paramName)) {
            content = project.getProperties().get(paramName)
            LOG.debug(MARKER, "From command line: $paramName : $content")
        } else if (hasEnvProperty(paramName)) {
            content = envProperties.getProperty(paramName)
            LOG.debug(MARKER, "From env properties: $paramName : $content")
        } else {
            content = project.extensions.getByName('cify').getProperties().get(paramName)
            if (content == null) {
                content = project.extensions.getByName('reporter').getProperties().get(paramName)
            }
            LOG.debug(MARKER, "Using default value: $paramName : $content")
        }
        content
    }

    /**
     * Sets threads count, how many threads in parallel
     */
    private void setThreads() {
        LOG.debug(MARKER, "Set threads number")

        String value = getValue('threads')
        if (value.isInteger()) {
            project.cify.threads = value
        } else {
            throw new CifyPluginException("Parameter threads: $value is not integer!")
        }
    }

    /**
     * Sets glues for cucumber, where to find step definition classes
     */
    private void setGlue() {
        project.cify.gluePackages = getValue('gluePackages')
    }

    /**
     * Sets tags for cucumber, what tags to exclude/include
     */
    private void setTags() {
        getValue('tags').tokenize(",").each {
            if (!(it ==~ /@.+/ || it ==~ /~@.+/)) {
                throw new CifyPluginException("Wrong tags parameter! Tag " + it + " is invalid.")
            }
        }
        project.cify.tags = getValue('tags')
    }

    /**
     * Sets list of plugins, formatters
     */
    private void setPlugins() {
        project.cify.cucumberPlugins = getValue('cucumberPlugins')
    }

    /**
     * Add plugins, formatters
     */
    private void addPlugins() {
        String plugins = getValue('addPlugins')

        if (plugins) {
            project.cify.cucumberPlugins = project.cify.cucumberPlugins + "," + plugins
        }
    }

    /**
     * Sets a dry run parameter
     */
    private void setDryRun() {
        String value = getValue('dryRun')
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            project.cify.dryRun = value
        } else {
            throw new CifyPluginException("Wrong dryRun parameter: " + value + " Can be true or false.")
        }
    }

    /**
     * Sets a strict parameter
     */
    private void setStrict() {
        String value = getValue('strict')
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            project.cify.strict = value
        } else {
            throw new CifyPluginException("Wrong strict parameter: " + value + " Can be true or false.")
        }
    }

    /**
     * Sets a monochrome parameter
     */
    private void setMonochrome() {
        String value = getValue('monochrome')
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            project.cify.monochrome = value
        } else {
            throw new CifyPluginException("Wrong monochrome parameter: " + value + " Can be true or false.")
        }
    }

    /**
     * Sets a ignoreFailures parameter
     */
    private void setIgnoreFailures() {
        String value = getValue('ignoreFailures')
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            project.cify.ignoreFailures = value
        } else {
            throw new CifyPluginException("Wrong ignoreFailures parameter: " + value + " Can be true or false.")
        }
    }

    /**
     * Sets feature directory location
     */
    private void setFeatureDirs() {
        project.cify.featureDirs = getValue('featureDirs')
    }

    /**
     * Sets capabilities file path
     * */
    private void setCapabilitiesFilePath() {
        String value = getValue('capabilitiesFilePath')
        if (value == project.extensions.getByName('cify').getProperties().get("capabilitiesFilePath")) {
            return
        }
        if (!new File(value).exists() || new File(value).isDirectory()) {
            throw new CifyPluginException("Filename " + value + " does not exist, or there is a directory with same name.")
        }
        project.cify.capabilitiesFilePath = value
    }

    /**
     * Sets remote url
     * */
    private void setFarmUrl() {
        UrlValidator urlValidator = new UrlValidator()
        String value = getValue("farmUrl")
        if (urlValidator.isValid(value) || value.isEmpty()) {
            project.cify.farmUrl = value
        } else throw new CifyPluginException("Url " + value + " specified by farmUrl parameter is not valid!")
    }

    /**
     * Sets capabilities from command line
     * */
    private void setCommandLineCapabilities() {
        project.cify.capabilities = getValue("capabilities")
    }

    /**
     * Sets credentials for device farm service providers
     */
    private void setCredentials() {
        String credentials = getValue('credentials')
        try {
            new JsonSlurper().parseText(credentials)
        } catch (JsonException ex1) {
            throw new CifyPluginException("Credentials is not valid json object: $credentials", ex1)
        } catch (IllegalArgumentException ex2) {
            throw new CifyPluginException("Credentials must not be null or empty", ex2)
        }
        project.cify.credentials = credentials
    }

    /**
     * Sets re-run parameter
     * */
    private void setRerunFailedTests() {
        String rerunEnabled = getValue('rerunFailedTests')

        if (rerunEnabled.equalsIgnoreCase("true") || rerunEnabled.equalsIgnoreCase("false")) {
            project.cify.rerunFailedTests = rerunEnabled
        } else {
            throw new CifyPluginException("Wrong re-run parameter: " + rerunEnabled + " Can be true or false.")
        }
    }

    /**
     * Sets repeat count
     * */
    void setRepeat() {
        String repeatCount = getValue('repeat')

        if (repeatCount.isInteger()) {
            project.cify.repeat = repeatCount
        } else {
            throw new CifyPluginException("Parameter repeat: $repeatCount is not integer!")
        }
    }

    /**
     * Sets capabilities to current run
     * */
    private void setCapabilities() {
        project.cify.capabilitiesSet = CapabilityParser.generateCapabilitiesList(
                project.cify.capabilitiesFilePath.toString(),
                project.cify.farmUrl.toString(),
                project.cify.capabilities.toString()
        )
    }

    /**
     * Sets capabilities to current run
     * */
    private void setFeatures() {
        project.cify.features = filterFeatures(project.cify.featureDirs.toString(), project.cify.tags.toString())
    }

    /**
     * Sets video recording parameter to current run
     * */
    private void setVideoRecord() {
        String value = getValue('videoRecord')
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            project.cify.videoRecord = value
        } else {
            throw new CifyPluginException("Wrong videoRecord parameter: " + value + " Can be true or false.")
        }
    }

    /**
     * Sets video directory path to current run
     * */
    private void setVideoDir() {
        project.cify.videoDir = getValue('videoDir')
    }

    /**
     * Sets reporter authentication service name to current run
     * */
    private void setAuthService() {
        project.reporter.authService = getValue('authService')
    }

    /**
     * Sets reporter project name to current run
     * */
    private void setProjectName() {
        project.reporter.projectName = getValue('projectName')
    }

    /**
     * Sets reporter testsuite name to current run
     * */
    private void setSuiteName() {
        project.reporter.suiteName = getValue('suiteName')
    }

    /**
     * Sets reporter accessKey to current run
     * */
    private void setAccessKey() {
        project.reporter.accessKey = getValue('accessKey')
    }

    /**
     * Filters features with given tags
     * */
    private List filterFeatures(String featureDirs, String tags) {
        List foundFeatures = []
        List featureDirsList = featureDirs.split(',').collect()
        featureDirsList.each { String directory ->
            def features = project.fileTree(dir: directory).include '**/*.feature'

            features.each { File file ->
                if (GherkinFeatureParser.hasScenarios(file.path, [tags])) {
                    foundFeatures.add(file.path)
                }
            }
        }

        LOG.debug("Filtered features with tags: " + project.cify.tags + " and the result were: " + foundFeatures.toArray())
        foundFeatures
    }
}
