package io.cify.framework.runner.utils
/**
 * Created by FOB Solutions
 */
class CherkinFeatureParserTest extends GroovyTestCase {


    private final String GHERKIN =
            '''
    @all
    Feature: All applications and browsers tests

      As a tester I would like to open all applications and browsers
      in order to test them.

      @native
      Scenario Outline: User opens native application and clicks button
        Given user opens <application>
        When user clicks the button
        Then the button should be still visible

        Examples:
          | application |
          | Android app |
          | iOS app     |

      @web
      Scenario Outline: User opens web application and clicks button
        Given user opens <application>
        When user clicks the button
        Then the button should be still visible

        Examples:
          | application |
          | Chrome app  |
          | Safari app  |
        '''

    private final String VENUES_GHERKIN =
            '''
    @smoke
    Feature: Venues

      Scenario: User verifies that Venue can be entered
        Given user opens application
        When user opens Venue of Alexa mall
        Then Venue Alexa mall is displayed

      @android @browser
      Scenario: User verifies that Venue can be entered with android and browser
        Given user opens application
        When user opens Venue of Alexa mall
        Then Venue Alexa mall is displayed

      @browser @ios
      Scenario: User verifies that Venue can be entered with browser and ios
        Given user opens application
        When user opens Venue of Alexa mall
        Then Venue Alexa mall is displayed

      @android
      Scenario: User verifies that Venue can be entered with android
        Given user opens application
        When user opens Venue of Alexa mall
        Then Venue Alexa mall is displayed

      @TODO @browser
      Scenario: User verifies that Venue can be entered with browser
        Given user opens application
        When user opens Venue of Alexa mall
        Then Venue Alexa mall is displayed
            '''

    private final String NO_TAGS_GHERKIN =
            '''
    Feature: NoTags

      Scenario: User verifies that Venue can be entered
        Given user opens application
        When user opens Venue of Alexa mall
        Then Venue Alexa mall is displayed

      @browser
      Scenario: User verifies that Venue can be entered with android and browser
        Given user opens application
        When user opens Venue of Alexa mall
        Then Venue Alexa mall is displayed

      Scenario: User verifies that Venue can be entered with browser and ios
        Given user opens application
        When user opens Venue of Alexa mall
        Then Venue Alexa mall is displayed
            '''

    private final String COLLECTION_GHERKIN =
                '''
    Feature: Collection

        @ios @collectplace @smoke
        Scenario: User collects a place with iOS
            Given user opens iOS application
            And user signs in with credentials testx99@example.com, qqqqqq
            And user opens first suggestion PDC of search LAX
            When user collects place
            Then place is collected
                '''

    void testGherkinHasNoScenariosInBadFile() {
        shouldFail {
            GherkinFeatureParser.hasScenarios('no/file/here', ['@gherkin'])
        }
    }

    void testCountScenariosWithoutFilters() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', [])
        assert scenarios.size() == 2
    }

    void testCountScenariosFeatureTagFilter() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', ['@all'])
        assert scenarios.size() == 2
    }

    void testCountScenariosContainingTag() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', ['@native'])
        assert scenarios.size() == 1
    }

    void testFilterScenariosCollectionMatched() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(COLLECTION_GHERKIN, '', ['@smoke'])
        assert scenarios.size() == 1
    }

    void testFilterScenariosCollectionNotMatched() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(COLLECTION_GHERKIN, '', ['@smoke,@android'])
        assert scenarios.size() == 0
    }

    void testFilterScenariosCollectionMatchedWithNegative() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(COLLECTION_GHERKIN, '', ['~@android,@ios,@smoke'])
        assert scenarios.size() == 1
    }

    void testFilterScenariosMixedFilterWithoutRootTag() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(VENUES_GHERKIN, '', ['@browser,~@TODO'])
        assert scenarios.size() == 2
    }

    void testFilterScenariosMixedFilterWithRootTag() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(VENUES_GHERKIN, '', ['@smoke,~@TODO'])
        assert scenarios.size() == 4
    }

    void testFilterScenariosNegativeFilter() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(VENUES_GHERKIN, '', ['~@TODO,~@android'])
        assert scenarios.size() == 2
    }

    void testFilterScenariosPositiveFilter() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(VENUES_GHERKIN, '', ['@TODO,@android,@browser'])
        assert scenarios.size() == 0
    }

    void testFilterScenariosRootTag() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(VENUES_GHERKIN, '', ['@smoke,@browser'])
        assert scenarios.size() == 3
    }

    void testFilterScenariosOneNegative() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(VENUES_GHERKIN, '', ['~@TODO'])
        assert scenarios.size() == 4
    }

    void testFilterScenariosJustRootTag() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(VENUES_GHERKIN, '', ['@smoke'])
        assert scenarios.size() == 5
    }

    void testFilterScenariosNoTagsNegative() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(NO_TAGS_GHERKIN, '', ['@browser'])
        assert scenarios.size() == 1
    }

    void testFilterScenariosNoRootTagPositive() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(NO_TAGS_GHERKIN, '', ['~@browser'])
        assert scenarios.size() == 2
    }

    void testFilterScenariosNoTags() {
        List<Object> scenarios =  GherkinFeatureParser.getScenarios(NO_TAGS_GHERKIN, '', [])
        assert scenarios.size() == 3
    }

    void testCountScenariosContainingOneOfTags() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', ['@native, @web'])
        assert scenarios.size() == 2
    }

    void testCountScenariosContainingAllTags() {
        List<Object> scenarios = GherkinFeatureParser.getScenarios(GHERKIN, '', ['@native', '@web'])
        assert scenarios.size() == 0
    }

    void testGherkinHasScenarios() {
        assert GherkinFeatureParser.hasScenarios(GHERKIN, '', [])
    }

    void testGherkinHasNoScenarios() {
        assert !GherkinFeatureParser.hasScenarios(GHERKIN, '', ['@gherkin'])
    }

}
