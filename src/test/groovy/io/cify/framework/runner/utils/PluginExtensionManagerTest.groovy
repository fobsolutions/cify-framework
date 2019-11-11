package io.cify.framework.runner.utils

import io.cify.framework.runner.CifyPlugin
import io.cify.framework.runner.CifyPluginExtension
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * Created by FOB Solutions
 */

class PluginExtensionManagerTest extends GroovyTestCase {
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    private File propertiesFile
    private File capabilitiesFile
    private File fileWithMode

    Project project
    CifyPluginExtension defaultExtension
    CifyPluginExtension extension
    PluginExtensionManager manager

    String capabilities = "{\n" +
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
        CifyPlugin plugin = new CifyPlugin()
        project = ProjectBuilder.builder().build()
        plugin.apply(project)
        manager = new PluginExtensionManager(project)
        manager.setupParameters()
        extension = project.cify
        defaultExtension = new CifyPluginExtension()
        testProjectDir.create()
        propertiesFile = testProjectDir.newFile("env-test.envProperties")
        capabilitiesFile = testProjectDir.newFile(extension.capabilitiesFilePath)
        fileWithMode = testProjectDir.newFile("fileWithMode.json")
    }

    void testGetDefaultFeatureDir() {
        assert extension.featureDirs == defaultExtension.featureDirs
    }

    void testDefaultThreadCount() {
        assert extension.threads == defaultExtension.threads
    }

    void testDefaultGlue() {
        assert extension.gluePackages == defaultExtension.gluePackages
    }

    void testDefaultTags() {
        assert extension.tags == defaultExtension.tags
    }

    void testDefaultPlugins() {
        assert extension.cucumberPlugins == defaultExtension.cucumberPlugins
    }

    void testDefaultDryRun() {
        assert extension.dryRun == defaultExtension.dryRun
    }

    void testDefaultStrict() {
        assert extension.strict == defaultExtension.strict
    }

    void testDefaultMonochrome() {
        assert extension.monochrome == defaultExtension.monochrome
    }

    void testDefaultIgnoreFailures() {
        assert extension.ignoreFailures == defaultExtension.ignoreFailures
    }

    void testDefaultCapabilities() {
        assert defaultExtension.capabilities.isEmpty()
    }

    void testDefaultRecording() {
        assert !defaultExtension.videoRecord.isEmpty()
        assert extension.videoRecord == defaultExtension.videoRecord
    }

    void testDefaultVideoPath() {
        assert !defaultExtension.videoDir.isEmpty()
        assert extension.videoDir == defaultExtension.videoDir
    }

    void testHelpText() {
        String helpText = extension.helpText
        assert !helpText.isEmpty()
    }

    void testGetValueFromPropertiesFile() {
        project.ext.set("env", "test")
        writeToProperties([
                "threads": "20"
        ])
        InputStream inputStream = manager.readPropertiesFromFile(propertiesFile)
        manager.envProperties.load(inputStream)
        assert manager.getValue("threads") == "20"
    }

    void testGetValueFromCommandLine() {
        project.ext.set("threads", "15")
        assert manager.getValue("threads") == "15"
    }

    void testEnvExistsButFileDont() {
        project.ext.set("env", "test")
        shouldFail(FileNotFoundException) {
            manager.setEnvProperties()
        }
    }

    void testBothCommandLineAndFileExist() {
        project.ext.set("env", "test")
        project.ext.set("threads", "15")
        writeToProperties([
                "threads": "20"
        ])
        InputStream inputStream = manager.readPropertiesFromFile(propertiesFile)
        manager.envProperties.load(inputStream)
        assert manager.getValue("threads") == "15"
    }

    void testWithMissingCapabilitiesFile() {
        List<Capabilities> capabilitiesSet = project.cify.capabilitiesSet
        assert capabilitiesSet.size() == 0
    }

    void testWithCapabilities() {
        capabilitiesFile.write(capabilities)
        project.ext.set("capabilitiesFilePath", capabilitiesFile.getPath())
        manager.setupParameters()
        assert project.cify.capabilitiesSet.size() == 3
    }

    void testCapabilitiesFromCommandLine() {
        project.ext.set("capabilities", capabilities)
        manager.setupParameters()
        project.cify.capabilitiesSet.size() == 3
    }

    void testWithRemoteUrl() {
        project.ext.set("farmUrl", "https://www.fob-solutions.com")
        manager.setupParameters()

        project.cify.capabilitiesSet.each { Capabilities capabilities ->
            capabilities.getBrowser().each {
                it.get("remote") == "https://www.fob-solutions.com"
            }
            capabilities.getAndroid().each {
                it.get("remote") == "https://www.fob-solutions.com"
            }

        }
    }

    void testWithCapabilityWithRemoteAndRemoteParam() {
        capabilitiesFile.write(capabilities)
        project.ext.set("capabilitiesFilePath", capabilitiesFile.getPath())
        project.ext.set("farmUrl", "https://www.fob-solutions.com")
        manager.setupParameters()
        project.cify.capabilitiesSet.each { Capabilities capabilities ->
            capabilities.getBrowser().each {
                it.get("remote") == "https://www.fob-solutions.com"
            }
            capabilities.getAndroid().each {
                it.get("remote") == "https://www.fob-solutions.com"
            }
        }
    }

    void testValidateTagsParameter() {
        shouldFail {
            project.ext.set("tags", "~@Broken,@Android,~Chrome")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("tags", "~@Broken,@Android,Chrome")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("tags", "~@Broken,@Android,@")
            manager.setupParameters()
        }
    }

    void testValidateDryRunParameters() {
        shouldFail {
            project.ext.set("dryRun", "truu")
            manager.setupParameters()
        }
    }

    void testValidateStrictParameters() {
        shouldFail {
            project.ext.set("strict", "truu")
            manager.setupParameters()
        }
    }

    void testValidateMonochromeParameters() {
        shouldFail {
            project.ext.set("monochrome", "truu")
            manager.setupParameters()
        }
    }

    void testValidateIgnoreFailuresParameters() {
        shouldFail {
            project.ext.set("ignoreFailures", "truu")
            manager.setupParameters()
        }
    }

    void testValidateThreadsParameter() {
        shouldFail {
            project.ext.set("threads", "four")
            manager.setupParameters()
        }
    }

    void testValidateCapabilitiesFilePathParameter() {
        project.ext.set("capabilitiesFilePath", defaultExtension.capabilitiesFilePath)
        manager.setupParameters()
        assert manager.getValue("capabilitiesFilePath") == defaultExtension.capabilitiesFilePath

        shouldFail {
            project.ext.set("capabilitiesFilePath", "src/testtt/resources/caps.json")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("capabilitiesFilePath", "src/test/resources")
            manager.setupParameters()
        }
    }

    void testValidateRemoteUrlParameter() {
        shouldFail {
            project.ext.set("farmUrl", "http:/www.here.ee")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("farmUrl", "httpss://www.here.ee")
            manager.setupParameters()
        }
        shouldFail {
            project.ext.set("farmUrl", "www.here.ee")
            manager.setupParameters()
        }
    }

    void testRecord() {
        project.ext.set("videoRecord", "true")
        manager.setupParameters()
        assert project.cify.videoRecord == "true"
    }

    void testAuthService() {
        project.ext.set("authService", "test.com")
        manager.setupParameters()
        assert project.reporter.authService == "test.com"
    }

    void testProjectName() {
        project.ext.set("projectName", "project-2")
        manager.setupParameters()
        assert project.reporter.projectName == "project-2"
    }

    void testSuiteName() {
        project.ext.set("suiteName", "test-suite-2")
        manager.setupParameters()
        assert project.reporter.suiteName == "test-suite-2"
    }

    void testAccessKey() {
        project.ext.set("accessKey", "1q2w3e4r5t6y7u")
        manager.setupParameters()
        assert project.reporter.accessKey == "1q2w3e4r5t6y7u"
    }

    void testDefaultReporterParameters() {
        manager.setupParameters()
        assert project.reporter.suiteName == null
        assert project.reporter.projectName == null
        assert project.reporter.accessKey == null
        assert project.reporter.authService == "auth.cify.io"
    }

    void testRecordWithInvalid() {
        project.ext.set("videoRecord", "truu")
        shouldFail {
            manager.setupParameters()
        }
    }

    void testVideoPath() {
        project.ext.set("videoDir", "results/videos/")
        manager.setupParameters()
        assert project.cify.videoDir == "results/videos/"
    }

    private void writeToProperties(Map<String, String> propertiesMap) {
        Properties properties = new Properties()
        propertiesMap.each { k, v -> properties.setProperty(k, v) }

        propertiesFile.withWriterAppend('UTF-8') { fileWriter ->
            fileWriter.writeLine ''
            properties.each { key, value ->
                fileWriter.writeLine "$key=$value"
            }
        }
    }

    void testValidCredentialsCanBeSet() {
        project.ext.set("credentials", '{"password":"secret"}')
        manager.setupParameters()
        assert project.cify.credentials == '{"password":"secret"}'
    }

    void testWithInvalidJsonCredentialsFails() {
        shouldFail() {
            project.ext.set("credentials", "param1:value1}")
            manager.setupParameters()
        }
    }

    void testWithEmptyCredentialsFails() {
        shouldFail() {
            project.ext.set("credentials", "")
            manager.setupParameters()
        }
    }

    void testReRunParameter() {
        project.ext.set("rerunFailedTests", "true")
        manager.setupParameters()
        assert project.cify.rerunFailedTests == "true"
    }

    void testWithEmptyRerunParamFails() {
        shouldFail() {
            project.ext.set("rerunFailedTests", "")
            manager.setupParameters()
        }
    }

    void testDefaultReRunParameter() {
        assert project.cify.rerunFailedTests == "false"
    }
}
