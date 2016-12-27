package io.cify.framework.reporting

import groovy.json.internal.LazyMap

/**
 * This class is responsible to keep reporting information about test scenario
 */
class Scenario extends TestRun {

    String scenarioId
    String cucumberScenarioId
    String errorMessage
    List<LazyMap> deviceList = []
    List<Step> stepList = new ArrayList<>()

    Scenario() {}

    Scenario(String name, String scenarioId) {
        this.name = name
        this.cucumberScenarioId = scenarioId
        this.scenarioId = TestReportManager.generateId()
        this.startDate = System.currentTimeMillis()
    }
}
