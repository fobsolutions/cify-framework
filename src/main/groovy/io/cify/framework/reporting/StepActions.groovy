package io.cify.framework.reporting

class StepActions extends Step{

    public String actionId

    public StepActions(String name){
        this.name = name
        this.actionId = TestReportManager.generateId()
    }
}
