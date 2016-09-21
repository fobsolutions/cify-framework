1. <a href="#what">What Is Cify Framework?</a>
1. <a href="#usage">How To Use Cify Framework</a>

<a name="what" />
## What Is Cify Framework?

Cify Framework is part of a open source test automation framework called Cify. Framework is responsible for device management and creating WebDrivers.

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
    compile 'io.cify:cify-framework:1.0.0'
}
```
----------

### Actions

Actions are activities that user can do on specific page. To make it work on different platforms and screen sizes user can make interface for actions and implement it for every device group if needed.

#### Action interface example

```
  public interface ILoginActions {
     
     /**
     * Login with given credentials
     *
     * @param username - username
     * @param password - password
     */
    void login(String username, String password);

}
```

### Matchers

Matchers are assertions that user can do on specific page. To make it work on different platforms and screen sizes user can make interface for matchers and implement it for every device group if needed.

#### Matchers interface example

```
  public interface ILoginMatchers {
     
     /**
     * Checks if login error is displayed
     */
    void shouldHaveLoginError();

}
```

### UI Type

There is a parameter called UIType, if certain device have this capability element then framework will find correct implementation of actions or matchers from given path.

Let's say that user have 3 different device groups: Tablet, Web, Mobile. When user tries to find login activities implementations then he/she can call:
```
    /**
     * Gets login actions to given device
     *
     * @param device - device under test
     */
    public static ILoginActivities getLoginActivities(Device device) {
        return (ILoginActions) Factory.get(device, "com.path.to.your.implementation.package.LoginActions");
    }
```

### Actions/Matchers Implementation

When using this interface user must name classes for different platforms or screen sizes like defined in capabilities file UIType parameter. Each implementation class should have constructor with Device parameter, cause implementation must know which device it should send them.

#### Example

Let's say that the device under test have capability UIType with value Tablet.

Then...
```
    /**
     * Gets login actions to given device
     *
     * @param device - device under test
     */
    public static ILoginActivities getLoginActivities(Device device) {
        return (ILoginActions) Factory.get(device, "path.to.your.implementation.package.LoginActions");
    }
```
... this will return class named LoginActionsTablet

### DeviceManager

Device manager is center of the framework. It manages the active devices, creates devices and closes devices.

```
    /**
     * Creates WebDriver with main capability
     */
    DeviceManager.createDevice();

     /**
     * Creates WebDriver with given parameters
     */
    DeviceManager.createDevice("platform", "android");

     /**
     * Creates WebDriver with given parameters and gives it a codename
     * that user can use to call it afterwards
     */
    DeviceManager.createDevice("MyAndroiDevice", "platform", "android");

     /**
     * Gets active Device (first one)
     */
    public static Device getActiveDevice();

    /**
     * Gets active Device with codename
     */
    public static Device getActiveDevice("MyAndroidDevice");

    /**
     * Quits active Device with codename
     */
    public static Device quitDevice("MyAndroidDevice");

    /**
     * Quits all devices
     */
    public static Device quitDevices();
```

### Page Objects

Page objects manage WebElements. In this class there should be defined all WebElements that can be used within implementation methods.

#### Example of a PageObject class
```
public class LoginPage extends PageObjects {

    public LoginPage(Device device) {
        super(device);
    }

    @FindBy(id = "loginSubmit")
    @AndroidFindBy(id = "android.examples.cify.io.android_native_app:id/sign_in_button")
    @iOSFindBy(xpath = "//UIAApplication[1]/UIAWindow[1]/UIAButton[1]")
    private WebElement loginSubmit;

    @FindBy(id = "loginError")
    @AndroidFindBy(id = "android.examples.cify.io.android_native_app:id/errorText")
    @iOSFindBy(xpath = "//UIAApplication[1]/UIAWindow[1]/UIATextView[1]")
    private WebElement loginError;

    @FindBy(id = "loginUsername")
    @AndroidFindBy(id = "android.examples.cify.io.android_native_app:id/username")
    @iOSFindBy(xpath = "//UIATextField[1]")
    private WebElement loginUsername;

    @FindBy(id = "loginPassword")
    @AndroidFindBy(id = "android.examples.cify.io.android_naitive_app:id/password")
    @iOSFindBy(xpath = "//UIATextField[2]")
    private WebElement loginPassword;

    public WebElement getLoginSubmit() {
        return loginSubmit;
    }

    public WebElement getLoginError() {
        return loginError;
    }

    public WebElement getLoginUsername() {
        return loginUsername;
    }

    public WebElement getLoginPassword() {
        return loginPassword;
    }
}
```

@FindBy is used in Web and WebViews

@AndroidFindBy is used in native Android applications

@iOSFindBy is used in native iOS applications
