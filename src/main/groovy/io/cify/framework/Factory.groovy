package io.cify.framework

import io.cify.framework.annotations.Title
import io.cify.framework.core.CifyFrameworkException
import io.cify.framework.core.Device
import io.cify.framework.reporting.TestReportManager

import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.LogManager

/**
 * Factory class for getting page objects
 */

public class Factory implements InvocationHandler {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('FACTORY') as Marker

    public static final String CAPABILITY_UI_TYPE = "UIType"

    private Object obj

    /**
     * Factory constructor
     * */
    private Factory(Object obj) {
        this.obj = obj
    }

    /**
     * Gets proxy instance for specific class and device
     *
     * @param device device object
     * @param className class name
     *
     * @return Object
     * @throws CifyFrameworkException if failed to create proxy instance
     * */
    public static Object get(Device device, String className) {
        LOG.debug(MARKER, "Create new proxy instance for class $className and device $device")

        try {
            String uiType = device.getCapabilities().getCapability(CAPABILITY_UI_TYPE)
            if (uiType == null) {
                uiType = ""
            }
            LOG.debug(MARKER, "UIType: $uiType")

            String nameForClass = className + uiType
            Class<?> clazz = Class.forName(nameForClass)
            Object obj = clazz.newInstance(device)
            return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                    new Factory(obj))
        } catch (all) {
            throw new CifyFrameworkException("Unable to create new proxy instance", all)
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        LOG.debug(MARKER, "Invoke method $method with args $args")

        Object result = null

        Method annotationMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes())
        if (annotationMethod.getAnnotation(Title.class) != null) {
            LOG.debug(MARKER, "Title: {}", annotationMethod.getAnnotation(Title.class).value())
            TestReportManager.stepActionStarted(annotationMethod.getAnnotation(Title.class).value())
        }

        if (args != null && args.length > 0) {
            String argsString = args.findAll({ it != null }).join(", ")
            LOG.debug(MARKER, "Args: {}", argsString)
        }


        try {
            result = method.invoke(obj, args)
        } catch (InvocationTargetException e) {
            throw e.getTargetException()
        } finally {
            if (annotationMethod.getAnnotation(Title.class) != null) {
                TestReportManager.stepActionFinished()
            }
        }

        return result
    }
}