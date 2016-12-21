package io.cify.framework.reporting

/**
 * This class is responsible to keep reporting information about test step
 */
class Step extends Scenario {

    String stepId
    List<StepAction> stepActionsList = new ArrayList<>()

    Step(){}
    Step(String name){
        this.name = name
        this.stepId = TestReportManager.generateId()
    }
}
