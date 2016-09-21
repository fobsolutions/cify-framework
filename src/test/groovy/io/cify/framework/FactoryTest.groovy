package io.cify.framework

import io.cify.framework.core.models.Device

/**
 * Created by FOB Solutions
 */
class FactoryTest extends GroovyTestCase {

    void testFactoryGetAndInvoke(){
        String parameter = "FactoryTest"
        ActionsTestInterface atc = (ActionsTestInterface) Factory.get(new Device([:]), "io.cify.framework.FactoryTestClass")
        assert atc.shouldGetResult(parameter) == "method invoked with parameter:"+parameter
    }
}



