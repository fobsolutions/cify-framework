package io.cify.framework.reporting

/**
 * This class is responsible to keep reporting information about test run
 */
class TestRun {

    String testRunId
    String name
    String result
    long startDate
    long endDate
    long duration

    Scenario activeScenario
    List<Scenario> scenarioList = new ArrayList<>()

    TestRun(){}
    TestRun (String name) {
        this.name = name
        this.testRunId = TestReportManager.generateId()
        this.startDate = System.currentTimeMillis()
    }
}
