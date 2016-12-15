package io.cify.framework.reporting

class Step extends Scenario {

    public Step(){}

    public Step(String name){
        this.name = name
        this.id = TestRunManager.generateId()
    }

}
