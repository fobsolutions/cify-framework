package io.cify.framework.reporting

class TestRun {

    public String id
    public String name
    public Date startDate
    public Date endDate
    public String result

    Scenario activeScenario
    List<Scenario> scenarioList = new ArrayList<>()

    public TestRun(){}

    public TestRun (String name) {
        this.name = name
        this.id = TestRunManager.generateId()
        this.startDate = new Date()
    }
}
