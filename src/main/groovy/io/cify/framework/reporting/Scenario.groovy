package io.cify.framework.reporting

class Scenario extends TestRun {

    public String id
    public String name
    Step activeStep
    List<Step> stepList = new ArrayList<>()

    public Scenario(){}

    public Scenario (String name){
        this.name = name
        this.id = TestRunManager.generateId()
    }

}
