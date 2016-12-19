package io.cify.framework.reporting

class StepAction extends Step{

    public String actionId

    public StepAction(String name){
        this.name = name
        this.actionId = TestReportManager.generateId()
        this.startDate = System.currentTimeMillis()
    }
}
