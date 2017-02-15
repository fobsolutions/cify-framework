package io.cify.framework.reporting

/**
 * This class is responsible to keep reporting information about test run
 */
class TestRun {

    String projectName
    String suiteName
    String testrunId
    String cucumberTestrunId
    String name
    String result
    String capabilitiesId
    long startDate
    long endDate
    long duration

    Scenario activeScenario
    List<Scenario> scenarioList = new ArrayList<>()

    TestRun() {}

    TestRun(String name, String cucumberTestrunId) {
        this.name = name
        this.capabilitiesId = System.getProperty("capabilitiesId")
        this.suiteName = System.getProperty("suiteName")
        this.projectName = System.getProperty("projectName")
        this.cucumberTestrunId = cucumberTestrunId
        this.testrunId = TestReportManager.generateId()
        this.startDate = System.currentTimeMillis()
    }
}
