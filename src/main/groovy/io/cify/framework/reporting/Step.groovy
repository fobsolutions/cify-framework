package io.cify.framework.reporting

class Step extends Scenario {

    public String id
    public String name
    public String deviceId
    public String deviceCategory

    public Step(){}

    public Step(String name){
        this.name = name
        this.id = TestRunManager.generateId()
    }

}
