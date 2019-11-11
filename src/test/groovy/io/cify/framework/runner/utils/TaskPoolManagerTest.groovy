package io.cify.framework.runner.utils

import io.cify.framework.runner.CifyPlugin
import io.cify.framework.runner.tasks.CifyCucumberTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.testfixtures.ProjectBuilder

/**
 * Created by FOB Solutions
 */
class TaskPoolManagerTest extends GroovyTestCase {
    private Project project
    private CifyPlugin plugin
    private testTask = "testTask"
    private TaskPoolManager taskPoolManager
    public static final THREADCOUNT = 3

    void setUp() {
        plugin = new CifyPlugin()
        project = ProjectBuilder.builder().build()
        project.getPluginManager().apply('java')
        plugin.apply(project)
        project.task(testTask, type: CifyCucumberTask)
        taskPoolManager = new TaskPoolManager(project)
        new PluginExtensionManager(project).setupParameters()
    }

    void tearDown() {
        project.tasks.clear()
        TestTask.threadIds.clear()
        TestTask.count = 0
    }

    void testTaskExists() {
        assert taskPoolManager.taskExists(testTask)
    }

    void testTaskDoNotExist() {
        assertFalse(taskPoolManager.taskExists("DoNotExist"))
    }

    void testAddTask() {
        String taskName = "testAddTask"
        taskPoolManager.addTask(taskName, CifyCucumberTask, ["browser": "chrome"])
        assert project.tasks[taskName] instanceof CifyCucumberTask
    }

    void testAddTaskThatExists() {
        String taskName = "testAddTask"
        taskPoolManager.addTask(taskName, CifyCucumberTask)
        taskPoolManager.addTask(taskName, CifyCucumberTask)
        taskPoolManager.tasksPool.size() == 1
    }

    void testRunInParallel() {
        addTestTasks(10)
        taskPoolManager.runTasksInParallel(THREADCOUNT)
        runWithTimeout(500, 20) {
            assert TestTask.threadIds.size() == THREADCOUNT
        }
    }

    void testRunCount() {
        addTestTasks(10)
        taskPoolManager.runTasksInParallel(1)
        runWithTimeout(500, 2) {
            assert TestTask.count == taskPoolManager.tasksPool.size()
        }
    }

    void testEmptyTaskPool() {
        taskPoolManager.runTasksInParallel(THREADCOUNT)
        runWithTimeout(500, 2) {
            assert TestTask.threadIds.isEmpty()
        }
    }

    void testTaskPoolWithNegativeThreadsCount() {
        addTestTasks(5)
        shouldFail {
            taskPoolManager.runTasksInParallel(-4)
        }
    }

    void testWithReRunParameter() {
        project.ext.set('rerunFailedTests', 'true')
        new PluginExtensionManager(project).setupParameters()

        addFailingTask(2)
        taskPoolManager.runTasksInParallel(2)
    }

    /**
     * Runs assertion closure several times (in case assertion fails) with timeout between tries
     */
    private static void runWithTimeout(long milliseconds, int tryCount, Closure assertion) {
        for (i in 1..tryCount) {
            sleep(milliseconds)
            try {
                assertion.run()
                return
            } catch (AssertionError error) {
                if (i == tryCount) {
                    throw error
                }
            }
        }
    }

    /**
     * Adds tasks into task pool
     * */
    private void addTestTasks(int count) {
        for (int i = 0; i < count; i++) {
            taskPoolManager.addTask(i.hashCode() as String, TestTask, ['count': i.toString()])
        }
    }

    /**
     * Adds failing task to task pool
     * */
    private void addFailingTask(int count) {
        for (int i = 0; i < count; i++) {
            taskPoolManager.addTask(i.toString(), TestTask, ['fail': 'true'])
        }
    }
}

/**
 * Test task for testing parallel running
 * */
class TestTask extends DefaultTask {

    static List threadIds = new ArrayList()
    static int count = 0
    Map<String, String> taskParams = new HashMap<>()

    @TaskAction
    void exec() {
        ++count

        long id = Thread.currentThread().getId()
        if (!threadIds.contains(id)) {
            threadIds.add(id)
        }

        if (taskParams['fail'] == "true") {
            throw new CifyPluginException("Task failed")
        }
    }
}



