package io.cify.framework.runner.utils

import groovyx.gpars.GParsPool
import io.cify.framework.runner.ReporterExtension
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * This manager is responsible for handling pool of tasks
 *
 * Created by FOB Solutions
 */

class TaskPoolManager {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('TASK POOL MANAGER') as Marker

    Project project
    List tasksPool = []

    TaskPoolManager(Project project) {
        this.project = project
    }

    /**
     * Checks if task already exists in project
     *
     * @param taskName task name to check
     *
     * @return boolean
     * */
    boolean taskExists(String taskName) {
        LOG.debug(MARKER, "Check if task $taskName already exists in the project")

        if (project.tasks.findByName(taskName) != null) {
            LOG.debug(MARKER, "Task $taskName already exists in the project")
            return true
        } else {
            LOG.debug(MARKER, "No task $taskName found in the project")
            return false
        }
    }

    /**
     * Adds task to task pool
     *
     * @param taskName task name to add
     * @param task task type to add
     * */
    void addTask(String taskName, Class task, Map<String, String> params) {
        LOG.debug(MARKER, "Add task $taskName $task with parameters $params to task pool")

        if (!taskExists(taskName) || !task instanceof Task) {
            project.task(taskName, type: task) {
                taskParams = params
            }
            tasksPool.add(project.tasks[taskName])
        }
    }

    /**
     * Add task with empty params list
     * */
    void addTask(String taskName, Class task) {
        addTask(taskName, task, [:])
    }

    /**
     * Runs tasks in parallel with given thread count
     *
     * @param threadCount count to run in parallel
     * */
    void runTasksInParallel(int threadCount) {
        try {
            LOG.debug(MARKER, "Run tasks in parallel")
            LOG.debug(MARKER, "Task pool contains " + tasksPool.size() + " tasks")
            LOG.debug(MARKER, "Number of threads: " + threadCount)

            if (isReporting() && tasksPool.size() > 0) {
                HashMap<String, String> params = [:]
                params.putAll(tasksPool.last().taskParams as Map)
                params.put('suiteFinished', 'true')
                addTask('endTask', tasksPool.last().getClass(), params)
            }

            List failedTasks = []
            GParsPool.withPool(threadCount) {

                tasksPool.eachParallel {
                    try {
                        if (it.taskParams['suiteFinished'] != "true") {
                            it.execute()
                        }
                    } catch (all) {
                        LOG.error(MARKER, "Failed to run task ${it.name}. Cause: ${all.message}")
                        failedTasks.add(it)
                    }
                }
                if (isReporting() && failedTasks.isEmpty() || !project.cify.rerunFailedTests) {
                    try {
                        tasksPool.find { it.taskParams['suiteFinished'] == 'true' }?.execute()
                    } catch (all) {
                        LOG.error(MARKER, "Failed to run end of suite task ${it.name}. Cause: ${all.message}")
                    }
                }

                if (!failedTasks.isEmpty() && project.cify.rerunFailedTests) {
                    LOG.debug(MARKER, "Re-running failed cases")
                    LOG.debug(MARKER, "Failed task pool contains " + failedTasks.size() + " tasks")
                    LOG.debug(MARKER, "Number of threads: " + threadCount)

                    if (isReporting()) {
                        failedTasks.add(tasksPool.find { it.taskParams['suiteFinished'] == 'true' })
                    }

                    failedTasks.eachParallel {
                        try {
                            if (it.taskParams['suiteFinished'] != "true") {
                                it.execute()
                            }
                        } catch (all) {
                            LOG.error(MARKER, "Re-running for task $it.name failed cause $all")
                        }
                    }
                    if (isReporting()) {
                        try {
                            failedTasks.find { it.taskParams['suiteFinished'] == 'true' }?.execute()
                        } catch (all) {
                            LOG.error(MARKER, "Re-running failed tasks finished. Failed to run end of suite task. Cause: " + all.message)
                        }
                    }
                }
            }
        } catch (all) {
            throw new CifyPluginException("TaskPoolManager: Exception occurred when executing tasks in parallel. Cause: " + all.message)
        }
    }

    /**
     * Returns true if all Reporter parameters are provided
     * @return
     */
    private boolean isReporting() {
        ReporterExtension reporterExtension = project.reporter as ReporterExtension
        return (reporterExtension.accessKey && reporterExtension.projectName
                && reporterExtension.suiteName && reporterExtension.authService)
    }
}
