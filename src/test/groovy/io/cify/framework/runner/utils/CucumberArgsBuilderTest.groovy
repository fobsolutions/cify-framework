package io.cify.framework.runner.utils

import io.cify.framework.runner.ReporterExtension

class CucumberArgsBuilderTest extends GroovyTestCase {

    CucumberArgsBuilder cucumberArgsBuilder

    void setUp() {
        cucumberArgsBuilder = new CucumberArgsBuilder("testTask", new ReporterExtension())
    }

    void testWithoutConfigurationBuildReturnsEmpty() {
        assert cucumberArgsBuilder.build() == []
    }

    void testWithTagsBuildReturnsArgsWithTags() {
        String tags = '~@Skip,@Todo'
        List expectedArgs = ['--tags', '~@Skip', '--tags', '@Todo']
        assert cucumberArgsBuilder.addTags(tags).build() == expectedArgs
    }

    void testWithPluginsBuildReturnsArgsWithPlugins() {
        def plugins = 'pretty'
        def formatExpected = ['--plugin', 'pretty']
        assert cucumberArgsBuilder.addPlugins(plugins).build() == formatExpected
    }

    void testWithDryRunBuildReturnsArgsWithDryRun() {
        assert cucumberArgsBuilder.setDryRun('true').build() == ['--dry-run']
    }

    void testWithStrictBuildReturnsArgsWithStrict() {
        assert cucumberArgsBuilder.setStrict('true').build() == ['--strict']
    }

    void testWithMonochromeBuildReturnsArgsWithMonochrome() {
        assert cucumberArgsBuilder.setMonochrome('true').build() == ['--monochrome']
    }

    void testWithGlueBuildReturnsArgsWithGlue() {
        def glue = 'path/to/glue'
        def expectedGlue = ['--glue', 'path/to/glue']
        assert cucumberArgsBuilder.addGlue(glue).build() == expectedGlue
    }

    void testWithFeatureDirBuildReturnsArgsWithFeatureDir() {
        def features = 'path/to/features'
        assert cucumberArgsBuilder.addFeatureDir(features).build() == [features]
    }

    void testWithAllOptionsBuildReturnsAllOptions() {
        def tags = '~@Skip,@Todo'
        def glue = 'path/to/glue'
        def plugins = 'pretty'
        def features = 'path/to/features'
        def expected = ['path/to/features', '--tags', '~@Skip', '--tags', '@Todo', '--plugin', 'pretty', '--glue', 'path/to/glue', '--dry-run', '--monochrome', '--strict']
        assert cucumberArgsBuilder.addFeatureDir(features).addTags(tags).addPlugins(plugins).addGlue(glue).setDryRun('true').setStrict('true').setMonochrome('true').build() == expected
    }

    void testWithAllEmptyBuildReturnsEmpty() {
        assert cucumberArgsBuilder.addFeatureDir('').addTags('').addPlugins('').addGlue('').setDryRun('false').setStrict('false').setMonochrome('false').build() == []
    }
}