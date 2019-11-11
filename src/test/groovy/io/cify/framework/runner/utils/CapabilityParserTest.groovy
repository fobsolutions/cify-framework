package io.cify.framework.runner.utils

import groovy.json.internal.LazyMap
import io.cify.framework.runner.CifyPlugin
import io.cify.framework.runner.CifyPluginExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * Created by FOB Solutions
 */
class CapabilityParserTest extends GroovyTestCase {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File capabilitiesFile

    Project project
    PluginExtensionManager manager
    CifyPlugin plugin

    String capabilities = "{\n" +
            "  \"defaults\": {\n" +
            "    \"android\": {\n" +
            "      \"version\": \"5.1\"\n" +
            "    },\n" +
            "    \"ios\": {\n" +
            "      \"version\": \"9.3\"\n" +
            "    },\n" +
            "    \"browser\": {\n" +
            "      \"version\": \"48\",\n" +
            "      \"type\": \"chrome\"\n" +
            "    }\n" +
            "  },\n" +
            "\n" +
            "  \"capabilities\": {\n" +
            "    \"browser\": [\n" +
            "      {\n" +
            "        \"version\": \"44\",\n" +
            "        \"type\": \"safari\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"version\": \"12\",\n" +
            "        \"type\": \"opera\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"version\": \"6.0\",\n" +
            "        \"type\": \"android\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"android\": [\n" +
            "      {\n" +
            "        \"version\": \"6.0\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}"

    void setUp() {
        plugin = new CifyPlugin()
        testProjectDir.create()
        capabilitiesFile = testProjectDir.newFile(new CifyPluginExtension().capabilitiesFilePath)
        project = ProjectBuilder.builder().build()
        plugin.apply(project)
        manager = new PluginExtensionManager(project)
    }

    void testGetCapabilitiesVariations() {
        LazyMap capabilitiesMap = CapabilityParser.readFromContent(capabilities) as LazyMap
        List capabilities = CapabilityParser.getCapabilitiesVariations(capabilitiesMap)
        assert capabilities.size() == 3
    }

    void testGenerateCapabilitiesList() {
        Capabilities item

        createCapabilitiesFile(null, true)
        List capabilitiesListNull = CapabilityParser.generateCapabilitiesList(capabilitiesFile.absolutePath,"","")
        assert capabilitiesListNull.size() == 8

        createCapabilitiesFile(CapabilityParser.STRATEGY_VARIATIONS, true)
        List<Capabilities> capabilitiesListVariations = CapabilityParser.generateCapabilitiesList(capabilitiesFile.absolutePath,"","")
        assert capabilitiesListVariations.size() == 8

        capabilitiesListVariations.each {
            item = it as Capabilities
            assert item.browser.size() + item.android.size() + item.ios.size() == 3
        }

        createCapabilitiesFile(CapabilityParser.STRATEGY_ONE_BY_ONE, true)
        List capabilitiesListOneByOne = CapabilityParser.generateCapabilitiesList(capabilitiesFile.absolutePath,"","")
        assert capabilitiesListOneByOne.size() == 7

        capabilitiesListOneByOne.each {
            item = it as Capabilities
            assert item.browser.size() + item.android.size() + item.ios.size() == 1
        }

        createCapabilitiesFile(CapabilityParser.STRATEGY_ALL_IN_ONE, true)
        List capabilitiesListAllInOne = CapabilityParser.generateCapabilitiesList(capabilitiesFile.absolutePath,"","")
        assert capabilitiesListAllInOne.size() == 1

        item = capabilitiesListAllInOne.get(0) as Capabilities
        assert item.browser.size() + item.android.size() + item.ios.size() == 7
    }

    void testGetCapabilitiesWithEmptyList() {
        List capabilities = CapabilityParser.getCapabilitiesList([], new LazyMap())
        assert capabilities.isEmpty()
    }

    void testReadCapabilitiesFromUrl() {
        String path = "http://www.fob-solutions.com/"
        shouldFail {
            CapabilityParser.getCapabilitiesFromFile(path)
        }
    }

    void testGetCapabilitiesWithIncorrectInput() {
        shouldFail {
            CapabilityParser.getCapabilitiesList(["capability1": "value1", "capability2": "value2"] as List, new LazyMap())
        }
    }

    void testGetCapabilitiesWithCustomParameters() {
        LazyMap lazyMap = CapabilityParser.readFromContent(capabilities) as LazyMap
        List capabilities = CapabilityParser.getCapabilitiesVariations(lazyMap)
        LazyMap extras = new LazyMap()
        extras.put("test", "testValue")
        List capabilitiesList = CapabilityParser.getCapabilitiesList(capabilities, extras)
        capabilitiesList.each { def capabilitiesObject ->
            capabilitiesObject.getIos().each {
                assert it.get("test") != null
            }
            capabilitiesObject.getAndroid().each {
                assert it.get("test") != null
            }
            capabilitiesObject.getBrowser().each {
                assert it.get("test") != null
            }
        }
    }

    private createCapabilitiesFile(String strategy, boolean hasContent) {
        String str = getCapabilitiesStr(strategy, hasContent)
        capabilitiesFile.write(str)
    }

    private static String getCapabilitiesStr(String strategy, boolean hasContent) {
        strategy = strategy ? "  \"strategy\": \"$strategy\"," : ""
        String content = ""
        if (hasContent) {
            content = "  \"capabilities\": {" +
                "    \"browser\": [" +
                "      {" +
                "        \"version\": \"48\"," +
                "        \"capability\": \"chrome\"" +
                "      }," +
                "      {" +
                "        \"version\": \"77\"," +
                "        \"capability\": \"firefox\"" +
                "      }," +
                "      {" +
                "        \"version\": \"44\"," +
                "        \"capability\": \"safari\"" +
                "      }," +
                "      {" +
                "        \"version\": \"6.0\"," +
                "        \"capability\": \"android\"" +
                "      }" +
                "    ]," +
                "    \"ios\": [" +
                "      {" +
                "        \"version\": \"9.3.5\"," +
                "        \"capability\": \"ipad\"" +
                "      }," +
                "      {" +
                "        \"version\": \"10.0.0\"," +
                "        \"capability\": \"iphone\"" +
                "      }" +
                "    ]," +
                "    \"android\": [" +
                "      {" +
                "        \"version\": \"6.0\"," +
                "        \"capability\": \"android\"" +
                "      }" +
                "    ]" +
                "  }"
        }
        String capabilities = "{" +
                strategy +
                content
                "}"
        return capabilities
    }
}
