[ ![Download](https://api.bintray.com/packages/fobsolutions/io.cify/cify-framework/images/download.svg) ](https://bintray.com/fobsolutions/io.cify/cify-framework/_latestVersion)

1. <a href="#what">What Is Cify Framework?</a>
2. <a href="#usage">How To Use Cify Framework</a>

<a name="what" />
## What Is Cify Framework?

Cify Framework is part of an open source test automation tool called Cify. Framework is responsible for managing communication with devices, and handling device actions (click, touch, tap, fillIn, sendKeys etc.) independently from device platform.

All examples are written in Groovy, optionally users can use Java.

<a name="usage" />
## How To Use Cify Framework

### Installation
Add Gradle dependency to your project as following:

```
repositories {
    maven {
        url "http://fobsolutions.bintray.com/io.cify"
    }
}

dependencies {
    // Check the latest version above
    compile 'io.cify:cify-framework:1.2.8'
}
```

### Framework configuration

When framework is used as a standalone project, then user must provide framework configuration.

Framework configuration must be defined in **configuration.json** file located in project root.
Configuration must contain all needed devices and their capabilities.
Optionally, configuration could contain framework parameters. 

**configuration.json** example:
```
{
 {
   "videoRecord": true,
   "videoDir": "build/cify/videos/",
   "capabilities": {
     "android": {
       "capability": "android",
       "UIType": "MobileAndroidApp",
       "deviceName": "Android Device",
       "app": "src/test/resources/applications/DemoApplication.apk",
       "fullReset": "true",
       "remote": "http://192.168.99.100:4444/wd/hub"
     },
     "browser": {
       "UIType": "DesktopWeb",
       "capability": "chrome"
     },
     "ios": {
       "capability": "iphone",
       "automationName": "XCUITest",
       "browserName": "",
       "UIType": "MobileIOSApp",
       "deviceName": "iOS Device",
       "app": "src/test/resources/applications/DemoApplication.ipa",
       "fullReset": "true",
       "autoAcceptAlerts": "true",
       "realDeviceLogger": "/usr/local/lib/node_modules/deviceconsole/deviceconsole",
       "showXcodeLog": "true",
       "remote": "http://192.168.99.100:4444/wd/hub"
     }
   }
 }

```


**videoRecord** - Enable web driver video recording.

**videoDir** - Directory where videos are saved.

**capabilities** - Are used when user right clicks on scenarios or feature and press run. Capability with given category is taken and triggered.

#### Override capabilities values with system/environment variables

User can override capability values with environment variables or system properties.

**Priority level:**

 - Environment variable
 - System property
 
Example of replaceable capability

```
    "remote": "http://<replaceProperty:SAUCELABS_USERNAME>:<replaceProperty:SAUCELABS_ACCESSKEY>@ondemand.saucelabs.com:80/wd/hub"
```

In this case system will look for SAUCELABS_USERNAME, SAUCELABS_ACCESSKEY properties from environment or system properties and replaces the value.

If there are no matches, then exception is thrown.


### Actions

Actions are activities that user can do on specific page. To make it work on different platforms and screen sizes user can make interface for actions and implement it for every device group if needed.

#### Action interface example

```
  interface ILoginActions {
     
     /**
     * Login with given credentials
     *
     * @param username username
     * @param password password
     */
    void login(String username, String password)
    
    /**
    * Checks if user is logged in
    */
    boolean isUserLoggedIn()
    
     /**
     * Checks if error is visible
     */
    boolean isLoginErrorVisible()

}
```

### UI Type

There is a parameter called UIType, if certain device have this capability element then framework will find correct implementation of actions or matchers from given UIType.

Let's say that user have 3 different device groups: Desktop, Mobile, OldMobile. When user tries to find login activities implementations then he/she can call:
```
    /**
     * Gets login actions to given device
     *
     * @param device device under test
     *
     * @return ILoginActions
     */
    static ILoginActions getLoginActivities(Device device) {
        (ILoginActions) Factory.get(device, "com.path.to.your.implementation.package.LoginActions")
    }
```

The framework will automatically search for one of the following classes LoginActionsDesktop, LoginActionsMobile or LoginActionsOldMobile depending on given device UIType. 

If no UIType specified the framework will search for LoginActions class.


### Actions Implementation

When using implementation classes for different platforms or screen sizes based on device UIType parameter, each class should have constructor with Device parameter. The framework will provided correct device automatically.

```
   class AccountActionsDesktopWeb implements IAccountActions {
   
       private Device device
       private SignInPageObjects signInPageObjects
   
       AccountActionsDesktopWeb(Device device) {
           this.device = device
           this.signInPageObjects = new SignInPageObjects(device)
       }
   
       /**
        * Clicks on email field and enters given email
        * @param email users email
        */
       @Override
       @Title("Enter email")
       void enterEmail(String email) {
           click(signInPageObjects.getEmailField())
           sendKeys(signInPageObjects.getEmailField(), email)
       }
    }
    
```

### DeviceCategory

The framework supports 3 device category:

1. Browser - desktop and mobile web
2. Android - Android native applications
3. iOS - iOS native applications



### DeviceManager

Device manager is center of the framework. It creates new devices, manages active devices and closes unused.

```
     /**
     * Creates device of given category
     */
    Device createDevice(DeviceCategory category)
    
    /**
     * Creates device with unique id
     */
    Device createDevice(DeviceCategory category, String deviceId)

     /**
     * Gets active Device (first one)
     */
    Device getActiveDevice()
    
    /**
     * Gets active Device of given category(first one)
     */
    Device getActiveDevice(DeviceCategory category)

    /**
     * Gets active Device with deviceId
     */
    Device getActiveDevice(deviceId)

    /**
     * Quits all devices
     */
    void quitAllDevices()
```


### Page Objects

Page objects describe elements of native applications and web pages. All elements should be describe here to be correctly used within implementation methods.

#### Example of a PageObject class
```
class LoginPage extends PageObjects {

    public LoginPage(Device device) {
        super(device)
    }

    @FindBy(id = "loginSubmit")
    @AndroidFindBy(id = "android.examples.cify.io.android_native_app:id/sign_in_button")
    @iOSFindBy(xpath = "//UIAApplication[1]/UIAWindow[1]/UIAButton[1]")
    WebElement loginSubmit

    @FindBy(id = "loginError")
    @AndroidFindBy(id = "android.examples.cify.io.android_native_app:id/errorText")
    @iOSFindBy(xpath = "//UIAApplication[1]/UIAWindow[1]/UIATextView[1]")
    WebElement loginError

    @FindBy(id = "loginUsername")
    @AndroidFindBy(id = "android.examples.cify.io.android_native_app:id/username")
    @iOSFindBy(xpath = "//UIATextField[1]")
    WebElement loginUsername

    @FindBy(id = "loginPassword")
    @AndroidFindBy(id = "android.examples.cify.io.android_naitive_app:id/password")
    @iOSFindBy(xpath = "//UIATextField[2]")
    WebElement loginPassword
}
```

@FindBy is used in Web and WebViews

@AndroidFindBy is used in native Android applications

@iOSFindBy is used in native iOS applications
