package io.cify.framework.runner.tasks

import groovy.json.JsonOutput
import groovy.json.StringEscapeUtils
import io.cify.framework.runner.CifyPluginExtension
import io.cify.framework.runner.Constants
import io.cify.framework.runner.ReporterExtension
import io.cify.framework.runner.utils.CucumberArgsBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.gradle.api.tasks.JavaExec

/**
 * This task is responsible for passing right parameters to CucumberRunner
 *
 * Created by FOB Solutions
 */

class CifyCucumberTask extends JavaExec {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CIFY CUCUMBER TASK') as Marker

    final static String JAVA_EXEC_MAIN = 'cucumber.api.cli.Main'

    public Map<String, Object> taskParams = [:]

    @Override
    void exec() {
        try {
            LOG.debug(MARKER, this.getName() + " started")

            args = getCucumberArgs(
                    project.cify as CifyPluginExtension,
                    project.reporter as ReporterExtension,
                    taskParams['taskName'] as String,
                    taskParams['featurePath'] as String
            )
            classpath = project.configurations.testRuntime + project.sourceSets.test.output + project.sourceSets.main.runtimeClasspath
            main = JAVA_EXEC_MAIN

            systemProperties = [
                    'task'          : taskParams['taskName'],
                    'capabilities'  : StringEscapeUtils.escapeJava(JsonOutput.toJson(taskParams['capabilities'])),
                    'videoRecord'   : taskParams['videoRecord'],
                    'videoDir'      : taskParams['videoDir'],
                    'credentials'   : taskParams['credentials'],
                    'runId'         : taskParams['runId'],
                    'projectName'   : taskParams['projectName'],
                    'suiteName'     : taskParams['suiteName'],
                    'accessKey'     : taskParams['accessKey'],
                    'authService'   : taskParams['authService'],
                    'capabilitiesId': taskParams['capabilitiesId'],
                    'suiteFinished': taskParams['suiteFinished']
            ]

            System.properties.each { k, v ->
                if (k.toString().startsWith(Constants.CIFY_SYSTEM_PROPERTY_PREFIX)) {
                    String key = k.toString().replace(Constants.CIFY_SYSTEM_PROPERTY_PREFIX, "")
                    systemProperties.put(key, v)
                }
            }

            super.exec()

            LOG.debug(MARKER, this.getName() + " finished")
        } catch (all) {
            LOG.error(MARKER, "Failed to execute " + this.getName(), all)
            throw all
        }
    }

    /**
     * Get cucumber arguments
     * @param cify extension class
     * @param reporter extension class
     * @param taskName task name
     * @param featurePath path for feature files
     * @return List of arguments
     */
    static List getCucumberArgs(CifyPluginExtension cify, ReporterExtension reporter, String taskName, String featurePath) {
        return new CucumberArgsBuilder(taskName, reporter)
                .addFeatureDir(featurePath ? featurePath : cify.featureDirs)
                .addTags(cify.tags)
                .addPlugins(cify.cucumberPlugins)
                .addGlue(cify.gluePackages)
                .setDryRun(cify.dryRun)
                .setStrict(cify.strict)
                .setMonochrome(cify.monochrome)
                .build()
    }
}