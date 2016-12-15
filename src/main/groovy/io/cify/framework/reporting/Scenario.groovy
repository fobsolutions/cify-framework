package io.cify.framework.reporting

class Scenario extends TestRun {

    public long duration
    public String errorMessage
    public String deviceId
    public String deviceCategory

    List<Step> stepList = new ArrayList<>()

    public Scenario(){}

    public Scenario (String name){
        this.name = name
        this.id = TestRunManager.generateId()
        this.startDate = new Date()
    }

}
