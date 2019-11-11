package io.cify.framework.runner.tasks

import io.cify.framework.runner.utils.PluginExtensionManager
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * This task is responsible for triggering parameter parsing
 *
 * Created by FOB Solutions
 */
class CifyExtensionTask extends DefaultTask {

    @TaskAction
    void exec() {
        try {
            new PluginExtensionManager(project).setupParameters()
        } catch (all) {
            throw all
        }
    }
}

