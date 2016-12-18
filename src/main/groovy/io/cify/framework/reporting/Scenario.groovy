package io.cify.framework.reporting

import groovy.json.JsonBuilder

class Scenario extends TestRun {

    public String scenarioId
    public String errorMessage
    public List<Map<String,String>> deviceList = new ArrayList<Map<String,String>>()
    List<Step> stepList = new ArrayList<>()

    public Scenario(){}

    public Scenario (String name){
        this.name = name
        this.scenarioId = TestRunManager.generateId()
        this.startDate = System.currentTimeMillis()
    }

    @Override
    String toString() {
        def jsonBuilder = new JsonBuilder()
        jsonBuilder.scenario(scenarioId:scenarioId,name:name,duration:duration,result:result,errorMessage:errorMessage)
        return jsonBuilder.toPrettyString()
    }
}
