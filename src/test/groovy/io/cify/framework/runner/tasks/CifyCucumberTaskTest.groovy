package io.cify.framework.runner.tasks

import io.cify.framework.runner.CifyPlugin
import io.cify.framework.runner.CifyPluginExtension
import io.cify.framework.runner.ReporterExtension
import io.cify.framework.runner.utils.CucumberArgsBuilder
import io.cify.framework.runner.utils.PluginExtensionManager
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class CifyCucumberTaskTest extends GroovyTestCase {

    CifyCucumberTask cucumberTask
    Project project

    CifyPluginExtension extension
    PluginExtensionManager parser

    void setUp() {
        CifyPlugin plugin = new CifyPlugin()
        project = ProjectBuilder.builder().build()
        plugin.apply(project)
        parser = new PluginExtensionManager(project)
        parser.setupParameters()
        extension = project.cify

        cucumberTask = project.task('cucumberTask', type: CifyCucumberTask) as CifyCucumberTask
        super.setUp()
    }

    void testArgsContainsAllCucumberOptions() {
        // set all boolean values true so the options would show up
        Map params = ["taskName": "testTask"]
        extension.dryRun = true
        extension.strict = true
        extension.strict = true

        List expected = new CucumberArgsBuilder(params["taskName"], project.reporter as ReporterExtension)
                .addFeatureDir(extension.featureDirs)
                .addTags(extension.tags)
                .addPlugins(extension.cucumberPlugins)
                .addGlue(extension.gluePackages)
                .setDryRun(extension.dryRun)
                .setStrict(extension.strict)
                .setMonochrome(extension.monochrome)
                .build()

        List actual = CifyCucumberTask.getCucumberArgs(extension, project.reporter as ReporterExtension, params["taskName"], null)

        assert expected == actual
    }
}
