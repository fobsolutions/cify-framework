package io.cify.framework.reporting

class Step extends Scenario {

    public String stepId
    public List<StepActions> stepActionsList = new ArrayList<>()

    public Step(){}

    public Step(String name){
        this.name = name
        this.stepId = TestReportManager.generateId()
    }
}
