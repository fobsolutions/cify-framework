package io.cify.framework.runner.utils

import io.cify.framework.runner.Constants
import io.cify.framework.runner.ReporterExtension

/**
 * This class is used to build cucumber arguments List.
 *
 * Created by FOB Solutions
 */
class CucumberArgsBuilder {

    static final String TAGS_OPTION = '--tags'
    static final String PLUGIN_OPTION = '--plugin'
    static final String DRYRUN_OPTION = '--dry-run'
    static final String MONOCHROME_OPTION = '--monochrome'
    static final String STRICT_OPTION = '--strict'
    static final String GLUE_OPTION = '--glue'
    List tags = []
    List plugins = []
    List glue = []
    boolean dryRun
    boolean strict
    boolean monochrome
    String featureDir
    String taskName

    ReporterExtension reporterExtension

    CucumberArgsBuilder(String taskName, ReporterExtension reporterExtension) {
        this.taskName = taskName
        this.reporterExtension = reporterExtension
    }

    /**
     * Adds tags arguments. Currently only AND is supported e.g. Scenarios with @important AND @billing
     * @param tags Cucumber tags
     * @return
     */
    CucumberArgsBuilder addTags(String tags) {
        if (tags) {
            List tagsList = tags.tokenize(',')
            tagsList.each {
                this.tags << TAGS_OPTION
                this.tags << it
            }
        }
        this
    }

    /**
     * Adds plugins to the list.
     * @param plugins
     * @return
     */
    CucumberArgsBuilder addPlugins(String plugins) {
        if (plugins) {
            List pluginsList = plugins.tokenize(',')
            pluginsList.each {
                this.plugins << PLUGIN_OPTION

                if (it.toString().startsWith("json:") || it.toString().contains("CifyJSONFormatter") && !it.toString().endsWith(".json")) {
                    it = it + taskName + ".json"
                } else if (it.toString().startsWith("junit:") && !it.toString().endsWith(".xml")) {
                    it = it + taskName + ".xml"
                } else if (it.toString() == "cify") {
                    it = "io.cify.plugins.CifyReporterPlugin:build/cify/reports/cify/" +  taskName + ".json"
                }

                this.plugins << it
            }

            if (reporterExtension.accessKey && reporterExtension.projectName
                    && reporterExtension.suiteName && reporterExtension.authService) {
                this.plugins << PLUGIN_OPTION
                this.plugins << Constants.REPORTER_PLUGIN_PATH
            }
        }
        this
    }

    /**
     * Adds glue files locations.
     * @param glue
     * @return
     */
    CucumberArgsBuilder addGlue(String glue) {
        if (glue) {
            List glueList = glue.tokenize(',')
            glueList.each {
                this.glue << GLUE_OPTION
                this.glue << it
            }
        }
        this
    }

    /**
     * Set dry run parameter
     *
     * @return cucumber args builder class
     * */
    CucumberArgsBuilder setDryRun(String dryrun) {
        this.dryRun = dryrun.toBoolean()
        this
    }

    /**
     * Set strict parameter
     *
     * @return cucumber args builder class
     * */
    CucumberArgsBuilder setStrict(String strict) {
        this.strict = strict.toBoolean()
        this
    }

    /**
     * Set monochrome parameter
     *
     * @return cucumber args builder class
     * */
    CucumberArgsBuilder setMonochrome(String monochrome) {
        this.monochrome = monochrome.toBoolean()
        this
    }

    /**
     * Set feature dir parameter
     *
     * @return cucumber args builder class
     * */
    CucumberArgsBuilder addFeatureDir(String featureDir) {
        if (featureDir) {
            this.featureDir = featureDir
        }
        this
    }

    /**
     * Builds list from parameters
     *
     * @return list of arguments
     * */
    List build() {
        List args = []

        if (featureDir) {
            args << featureDir
        }

        if (tags) {
            args << tags
        }

        if (plugins) {
            args << plugins
        }

        if (glue) {
            args << glue
        }

        if (dryRun) {
            args << DRYRUN_OPTION
        }

        if (monochrome) {
            args << MONOCHROME_OPTION
        }

        if (strict) {
            args << STRICT_OPTION
        }
        args?.flatten()
    }
}
