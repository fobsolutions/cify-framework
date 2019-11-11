package io.cify.framework.runner.tasks.junit

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.junit.runner.Computer
import org.gradle.internal.impldep.org.junit.runner.JUnitCore

/**
 * This task is responsible for passing right parameters to CucumberRunner
 *
 * Created by FOB Solutions
 */
class CifyJUnitTask extends Exec {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('CIFY JUNIT TASK') as Marker

    public Map<String, Object> taskParams = [:]

    @TaskAction
    void exec() {
        try {
            LOG.debug(MARKER, this.getName() + " started")

            Computer computer = new Computer()
            String classString = taskParams['testClassString']
            JUnitCore jUnitCore = new JUnitCore()
            Class testClass = Class.forName("io.cify.framework.runner.CifyPluginTest")
            jUnitCore.run(computer, testClass)

            LOG.debug(MARKER, this.getName() + " finished")
        } catch (all) {
            LOG.error(MARKER, "Failed to execute " + this.getName(), all)
            throw all
        }
    }
}
