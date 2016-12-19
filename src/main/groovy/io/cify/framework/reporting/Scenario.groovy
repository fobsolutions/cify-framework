package io.cify.framework.reporting

class Scenario extends TestRun {

    public String scenarioId
    public String errorMessage
    public List<Map<String,String>> deviceList = new ArrayList<Map<String,String>>()
    List<Step> stepList = new ArrayList<>()

    public Scenario(){}

    public Scenario (String name){
        this.name = name
        this.scenarioId = TestReportManager.generateId()
        this.startDate = System.currentTimeMillis()
    }
}
