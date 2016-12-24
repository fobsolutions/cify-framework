package io.cify.framework.reporting

/**
 * This class is responsible to keep reporting information about test scenario
 */
class Scenario extends TestRun {

    String scenarioId
    String errorMessage
    List<Map<String, String>> deviceList = new ArrayList<Map<String, String>>()
    List<Step> stepList = new ArrayList<>()

    Scenario() {}

    Scenario(String name) {
        this.name = name
        this.scenarioId = TestReportManager.generateId()
        this.startDate = System.currentTimeMillis()
    }
}
