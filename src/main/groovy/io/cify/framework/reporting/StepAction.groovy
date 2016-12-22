package io.cify.framework.reporting

/**
 * This class is responsible to keep reporting information about test step action
 */
class StepAction extends Step{

    String actionId

    StepAction(String name){
        this.name = name
        this.actionId = TestReportManager.generateId()
        this.startDate = System.currentTimeMillis()
    }
}
