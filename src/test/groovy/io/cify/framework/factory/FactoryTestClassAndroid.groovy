package io.cify.framework.factory

import io.cify.framework.annotations.Title
import io.cify.framework.core.Device

/**
 * Created by FOB Solutions
 */
class FactoryTestClassAndroid implements FactoryTestClassInterface {

    Device device;

    FactoryTestClassAndroid(Device device) {
        this.device = device;
    }

    @Override
    @Title("Return true")
    boolean getTrue(boolean isTrue) {
        return isTrue
    }
}
