package io.cify.framework.factory

import io.cify.framework.annotations.Title
import io.cify.framework.core.Device

/**
 * Created by FOB Solutions
 */
class FactoryTestClass implements FactoryTestClassInterface {

    Device device;

    FactoryTestClass(Device device) {
        this.device = device;
    }

    @Override
    @Title("Return true")
    boolean getTrue(boolean isTrue) {
        return isTrue
    }
}
