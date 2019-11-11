package io.cify.framework.runner.utils

/**
 * This class is responsible for filtering Gherkin files
 *
 * Created by FOB Solutions
 */
class GherkinFeatureFilter {

    private static List negative = []
    private static List positive = []
    private static boolean include = false
    private static boolean exclude = false
    private static int positiveFilterSize = 0
    private static def gherkinFeatureObject
    private static def gherkinRootTags

    /**
     * Removes scenarios from Gherkin object according to filter
     * @param gherkinObject
     * @param filters
     * @return filtered Gherkin feature object
     */
    static Object filterScenarios(Object gherkinObject, List filters) {
        if(!gherkinObject){
            throw new CifyPluginException('GherkinFeatureFilter: filterScenarios method failed, null Gherkin object')
        } else {
            initParameters(gherkinObject)
        }

        if (!filters) {
            return gherkinFeatureObject
        } else {
            parseFilters(filters)
        }

        List scenariosToRemove = matchGherkinScenariosWithFilter(gherkinFeatureObject)
        def newGherkinFeatureObject = removeScenarios(scenariosToRemove, gherkinFeatureObject)
        return newGherkinFeatureObject
    }

    private static initParameters(Object gherkinObject){
        negative.clear()
        positive.clear()
        positiveFilterSize = 0
        gherkinFeatureObject = gherkinObject.get(0)
        gherkinRootTags = gherkinObject.tags.get(0)
    }

    private static parseFilters(List filters){
        filters[0].tokenize(',').each {
            if (it.toString().trim().startsWith('~@')) {
                negative.add(it.trim().replace('~', ''))
            }
            if (it.toString().trim().startsWith('@')) {
                positive.add(it.trim())
            }
        }
        if (positive) {
            positiveFilterSize = positive.size()
        }
        negative.each {
            if (positive.contains(it)) {
                positive.remove(it)
            }
        }
    }

    private static List matchGherkinScenariosWithFilter(Object gherkinFeatureObject){
        List scenariosToRemove = []
        gherkinFeatureObject.elements.findAll { it.keyword == 'Scenario' }.eachWithIndex { item, index ->
            int matched = 0
            include = false
            exclude = false

            gherkinRootTags.each {
                if (!exclude) {
                    if (positive.contains(it.name)) {
                        matched++
                        include = true
                    }
                    if (negative.contains(it.name)) {
                        matched--
                        exclude = true
                        scenariosToRemove.add(index)
                    }
                }
            }

            if (!exclude) {
                item.tags.each {
                    if (!exclude) {
                        if (positive.contains(it.name)) {
                            matched++
                            include = true
                        }
                        if (negative.contains(it.name)) {
                            matched--
                            exclude = true
                            scenariosToRemove.add(index)
                        }
                    }
                }
            }

            if (!exclude && positiveFilterSize > 0 && positiveFilterSize > matched) {
                exclude = true
                scenariosToRemove.add(index)
            }
            if (!exclude && positiveFilterSize > 0 && !include) {
                exclude = true
                scenariosToRemove.add(index)
            }
        }
        return scenariosToRemove
    }

    private static removeScenarios(List scenariosToRemove, Object gherkinFeatureObject){
        if (scenariosToRemove && scenariosToRemove.size() > 0) {
            int removed = 0
            scenariosToRemove.each {
                gherkinFeatureObject.elements.remove(gherkinFeatureObject.elements[it - removed])
                removed++
            }
        }
        return gherkinFeatureObject
    }
}
