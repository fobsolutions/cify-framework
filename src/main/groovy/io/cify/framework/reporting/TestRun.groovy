package io.cify.framework.reporting

class TestRun {

    public String testRunId
    public String name
    public long startDate
    public long endDate
    public long duration
    public String result

    Scenario activeScenario
    List<Scenario> scenarioList = new ArrayList<>()

    public TestRun(){}

    public TestRun (String name) {
        this.name = name
        this.testRunId = TestReportManager.generateId()
        this.startDate = System.currentTimeMillis()
    }
}
