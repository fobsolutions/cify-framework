package io.cify.framework.runner

/**
 * Parameters used in the Cify plugin.
 *
 * Created by FOB Solutions
 */
class CifyPluginExtension {

    /**
     *  Directories to use as source for step definitions. Defaults to src/test/java
     */
    String gluePackages = ""

    /**
     * Directories to look for feature files. Defaults to [src/test/resources]
     */
    String featureDirs = "src/test/resources"

    /**
     * Tags used to filter which scenarios should be run. Defaults to ~@Broken,~@Skip,~@Unstable,~@Todo
     */
    String tags = "~@Broken,~@Skip,~@Unstable,~@Todo"

    /**
     * Output formats for cucumber test results. Defaults to 'pretty,json:build/cify/reports/json/'
     */
    String cucumberPlugins = "pretty,json:build/cify/reports/json/,junit:build/cify/reports/junit/"

    /**
     * Execute a test dry run without actually executing tests. Defaults to false
     */
    String dryRun = "false"

    /**
     * Strict mode, fail if there are pending or skipped tests. Defaults to false
     */
    String strict = "false"

    /**
     * Format output in single color.  Defaults to false
     */
    String monochrome = "false"

    /**
     * Whether to cause a build failure on any test failures. Defaults to false
     */
    String ignoreFailures = "false"

    /**
     * How many tests in parallel. Defaults to 1
     * */
    String threads = "1"

    /**
     * Capabilities file path. Defaults to capabilities.json
     * */
    String capabilitiesFilePath = "capabilities.json"

    /**
     * Remote URL for device farms, if set then used in RemoteWebDriver. Defaults to empty string
     * */
    String farmUrl = ""

    /**
     * Capabilities from command line
     * */
    String capabilities = ""

    /**
     * Enable video recording from command line. Defaults to false
     * */
    String videoRecord = "false"

    /**
     * Directory where videos are saved. Defaults to build/cify/videos/
     * */
    String videoDir = "build/cify/videos/"

    /**
     * Enable re-running failed tests after full suite. Defaults to false.
     * */
    String rerunFailedTests = "false"

    /**
     * Repeat matching tasks. Defaults to 1
     * */
    String repeat = 1

    /**
     * Generated capabilities list
     * */
    List capabilitiesSet = []

    /**
     * Credentials of device farm service providers
     */
    String credentials = "{}"

    /**
     * Feature files paths with given parameters
     * */
    List features = []

    /**
     * Help text to show user what options we have via command line
     * */
    String helpText =
            '''
    Options:

    Here you can find all the parameters we use inside our framework.
    You can change every parameter by sending it via command line or adding it into build script.

        Cify parameters:

            -Pthreads               Specify number of parallel threads. Default 1
                                    Usage:  ./gradlew cucumber -Pthreads=3

            -Penv                   Environment name to use.
                                    You can define all the parameters in envProperties file.
                                    Properties files should be located in root location, right next to build.gradle.
                                    If user passes env variable with value dev,
                                    then system will search for env-dev.envProperties file from root folder.
                                    Usage:  ./gradlew cucumber -Penv=dev

            -PcapabilitiesFilePath  Devices JSON file path. Defaults to capabilities.json
                                    Usage: ./gradlew cucumber -PcapabilitiesFilePath=capabilities.json

            -PfarmUrl               Remote URL for device farms, if set then used in RemoteWebDriver. Defaults to empty string
                                    Usage: ./gradlew cucumber -PfarmUrl=http://localhost:63342/

            -PvideoRecord           Record video for every device from creating driver til closing driver. Defaults to false
                                    Usage: ./gradlew cucumber -PvideoRecord=true

            -PvideoDir              Directory where videos are saved. Defaults to build/cify/videos
                                    Usage: ./gradlew cucumber -PvideoDir=project/videos/

            -PrerunFailedTests      Re-run failed tests
                                    Usage: ./gradlew cucumber -PrerunFailedTests=true

            -Prepeat                Repeat given tasks
                                    Usage: ./gradlew cucumber -Prepeat=5

        Cucumber parameters:

            -PgluePackages      Set a package to search step definitions in
                                Usage:  ./gradlew cucumber -PgluePackages=com/example/stepdefinitions,com/example2/stepdefinitions

            -PfeatureDirs       Set a package to search feature files,
                                Usage:  ./gradlew cucumber

            -Ptags              Run features/scenarios with certain tag only
                                Usage:  ./gradlew cucumber -Ptags=@smoke
                                        ./gradlew cucumber -Ptags=@android,@ios

            -PcucumberPlugins   Register a cucumber plugins
                                Usage:  ./gradlew cucumber -PcucumberPlugins=screenshot
                                        ./gradlew cucumber -PcucumberPlugins=screenshot,saucelabs

            -PdryRun            Execute a test dry run without actually executing tests. Defaults to false
                                Usage:  ./gradlew cucumber -PdryRun=false

            -Pstrict            Strict mode, fail if there are pending or skipped tests. Defaults to false
                                Usage:  ./gradlew cucumber -Pstrict=false

            -Pmonochrome        Format output in single color.  Defaults to false
                                Usage:  ./gradlew cucumber -Pmonochrome=false

            -PignoreFailures    Whether to cause a build failure on any test failures.
                                Usage:  ./gradlew cucumber -PignoreFailures=false

    Device usage:

    Here you can find information about running tests with and without capabilities file.

        Usable capabilities:
                            chrome
                            safari
                            opera
                            firefox
                            android
                            internetexplorer
                            ipad
                            iphone
                            phantomjs
                            edge

        Capabilities file:

            Every capability parameter will be added to DesiredCapability object.
            Every capability will be executed with all the tests provided.

            Every capability MUST HAVE one parameter:
            1) capability - system will use this to generate default desired capability for capability.
                            See available capabilities on "Usable capabilities" section.

            User can add as many additional parameters as needed (like in 3. capability object)
            and they will be added to DesiredCapability object.

            Valid capabilities file structure:
            {
              "defaults": {
                "android": {
                  "capability": "android",
                  "app": "path/to/your/app/DemoApp.apk,
                  "version": "5.1"
                },
                "ios": {
                  "capability": "iphone",
                  "app": "path/to/your/app/DemoApp.ipa,
                  "version": "9.3"
                },
                "browser": {
                  "version": "48",
                  "capability": "chrome"
                }
              },
              "set": {
                "browser": [
                  {
                    "version": "44",
                    "capability": "safari"
                  },
                  {
                    "version": "12",
                    "capability": "opera"
                  },
                  {
                    "version": "6.0",
                    "capability": "android"
                  }
                ],
                "ios": [
                  {
                    "capability": "ipad",
                    "app": "path/to/your/app/DemoApp.ipa
                    "version": "9.3.5"
                  }
                ]
              }
            }

            In this case there will be 3 different variations (tasks) to run:

            Safari 44, Android 5.1, iOS 9.3.5 on iPad
            Opera 12, Android 5.1, iOS 9.3.5 on iPad
            Android 6.0 Browser, Android 5.1, iOS 9.3.5 on iPad

            There is a possibility to pass capabilities from command line.

            Example:

            ./gradlew cucumber -Pcapabilities='{"set": {"browser":[{"version":"52","capability":"chrome"}]}}
        '''
}
