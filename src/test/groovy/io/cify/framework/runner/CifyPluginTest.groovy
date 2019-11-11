package io.cify.framework.runner

import io.cify.framework.runner.tasks.CifyExtensionTask
import io.cify.framework.runner.tasks.CifyTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder

/**
 * Created by FOB Solutions
 */
class CifyPluginTest extends GroovyTestCase {

    private Project project
    private CifyPlugin plugin

    void setUp() {
        project = ProjectBuilder.builder().build()
        plugin = new CifyPlugin()
        plugin.apply(project)
    }

    void testShouldCreateExtension() {
        assert project.hasProperty('cify')
    }

    void testApply() {
        assert project.tasks["cucumber"] instanceof CifyTask
        assert project.tasks["cifyhelp"] instanceof Task
        assert project.tasks["parameters"] instanceof CifyExtensionTask
    }

    void testReporterInit() {
        assert !project.reporter.runId.isEmpty()
        assert project.reporter.reporterPlugin == Constants.REPORTER_PLUGIN_PATH
    }
}
