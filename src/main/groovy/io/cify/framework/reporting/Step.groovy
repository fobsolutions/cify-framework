package io.cify.framework.reporting

class Step extends Scenario {

    public String stepId

    public Step(String name){
        this.name = name
        this.stepId = TestRunManager.generateId()
    }
}
