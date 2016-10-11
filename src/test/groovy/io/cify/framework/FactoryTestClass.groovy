package io.cify.framework

import io.cify.framework.annotations.Title
import io.cify.framework.core.models.Device

/**
 * Created by FOB Solutions
 */
class FactoryTestClass implements ActionsTestInterface{

    Device device;

    FactoryTestClass(Device device) {
        this.device = device;
    }

    @Override
    @Title("method should return string")
    String shouldGetResult(String parameter) {
        return "method invoked with parameter:"+parameter
    }
}
