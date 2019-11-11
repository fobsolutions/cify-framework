package io.cify.framework.runner

import io.cify.framework.runner.tasks.CifyExtensionTask
import io.cify.framework.runner.tasks.CifyTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

import static java.util.UUID.randomUUID

/**
 * Cify plugin.
 *
 * Created by FOB Solutions
 */
class CifyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("cify", CifyPluginExtension)

        project.extensions.create("reporter", ReporterExtension).with {
            runId = (System.currentTimeMillis() + "-" + randomUUID()) as String
            reporterPlugin = Constants.REPORTER_PLUGIN_PATH
        }

        project.task('parameters', type: CifyExtensionTask) {
            group = 'Cify'
            description = 'Reads and validates parameters, and saves them to a configuration file'
        }

        project.task('cucumber', type: CifyTask, dependsOn: ["build", project.tasks.parameters]) {
            group = 'Cify'
            description = 'Parses feature files, generates and executes Cucumber tasks'
        }

        project.task('cifyhelp') {
            group = 'Cify'
            description = 'Shows a list of available options'

            doFirst {
                println(project.cify.helpText)
            }
        }

        project.task('cloneDeviceFarm', type: Exec) {
            group = 'Cify'
            description 'Pulls device farm from GitHub into devicefarm directory'
            commandLine "git", "clone", "https://github.com/fobsolutions/cify-device-farm", "devicefarm"
        }
    }
}
