package io.cify.framework.runner.tasks

import io.cify.framework.runner.utils.Capabilities
import io.cify.framework.runner.utils.TaskPoolManager
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * This task is responsible for collecting all tasks together and triggering them in parallel
 *
 * Created by FOB Solutions
 */
class CifyTask extends DefaultTask {

    @TaskAction
    void exec() {
        try {
            TaskPoolManager taskPoolManager = new TaskPoolManager(project)

            List features = project.cify.features
            List capabilitiesSet = project.cify.capabilitiesSet
            String videoRecord = project.cify.videoRecord
            String videoDir = project.cify.videoDir
            String credentials = project.cify.credentials
            int repeatCount = project.cify.repeat as int
            String runId = project.reporter.runId
            String projectName = project.reporter.projectName
            String suiteName = project.reporter.suiteName
            String accessKey = project.reporter.accessKey
            String authService = project.reporter.authService
            String suiteFinished = project.reporter.suiteFinished

            features.each { String filePath ->
                File featureFile = new File(filePath)
                String featurePath = featureFile.path.replace(project.rootDir.toString() + '/', '')
                String featureName = featureFile.name

                capabilitiesSet.each { Capabilities capabilities ->

                    for (int i = 0; i < repeatCount; i++) {
                        String taskName = featureName + "_" + capabilities.toString() + "_" + i

                        Map params = [:]
                        params.put('taskName', taskName)
                        params.put('featurePath', featurePath)
                        params.put('capabilities', capabilities)
                        params.put('capabilitiesId', capabilities.toString())
                        params.put('videoRecord', videoRecord)
                        params.put('videoDir', videoDir)
                        params.put('credentials', credentials)
                        params.put('runId', runId)
                        params.put('projectName', projectName)
                        params.put('suiteName', suiteName)
                        params.put('accessKey', accessKey)
                        params.put('authService', authService)
                        params.put('suiteFinished', suiteFinished)
                        taskPoolManager.addTask(taskName, CifyCucumberTask, params)
                    }
                }
            }
            taskPoolManager.runTasksInParallel(project.cify.threads as Integer)
        }
        catch (all) {
            throw (all)
        }
    }
}
