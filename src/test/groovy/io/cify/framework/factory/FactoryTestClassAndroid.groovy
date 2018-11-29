package io.cify.framework.factory

import io.cify.framework.annotations.Title
import io.cify.framework.core.WebDriverDevice

/**
 * Created by FOB Solutions
 */
class FactoryTestClassAndroid implements FactoryTestClassInterface {

    WebDriverDevice device;

    FactoryTestClassAndroid(WebDriverDevice device) {
        this.device = device;
    }

    @Override
    @Title("Return true")
    boolean getTrue(boolean isTrue) {
        return isTrue
    }
}
