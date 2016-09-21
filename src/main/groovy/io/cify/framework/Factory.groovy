package io.cify.framework


import io.cify.framework.annotations.Title
import io.cify.framework.core.models.Device
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Factory class for getting page objects
 */
public class Factory implements InvocationHandler {

    private static final Logger LOG = LogManager.getLogger(Factory.class)

    private Object obj

    /**
     * Factory constructor
     * */
    private Factory(Object obj) {
        this.obj = obj
    }

    /**
     * Gets page object implementation class for specific device
     *
     * @param device - device object
     * @param className - classname
     * */
    public static Object get(Device device, String className) {
        String uiType = "";
        try {
            uiType = device.getCapabilityByName(Constants.UI_TYPE) != null ? device.getCapabilityByName(Constants.UI_TYPE) : ""
            LOG.debug("Creating new proxy instance for classname: " + className + uiType + " for device: " + device.getCapabilityByName(Constants.CAPABILITY_ID))
            String nameForClass = className + uiType
            Class<?> clazz = Class.forName(nameForClass)
            Object obj = clazz.newInstance(device)
            return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                    new Factory(obj))
        } catch (Exception e) {
            LOG.error("Unable to create new proxy instance for classname: " + className + uiType + ", class not found.")
            throw new RuntimeException(e)
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result


        Marker marker = MarkerManager.getMarker("ACTIONS")
        if (method.getName().contains("is") || method.getName().contains("should")) {
            marker = MarkerManager.getMarker("MATCHERS")
        }

        Method annotationMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes())
        String argsString = ""

        if (args != null && args.length > 0) {
            argsString = args.findAll({ it != null }).join(", ")
        }

        if (annotationMethod.getAnnotation(Title.class) != null) {
            LOG.debug(marker, "{}", annotationMethod.getAnnotation(Title.class).value())
        } else {
            LOG.debug(marker, "Executing")
        }
        LOG.debug(marker, "{}", argsString)


        try {
            result = method.invoke(obj, args)
            LOG.debug(marker, "Done")
        } catch (InvocationTargetException e) {
            throw e.getTargetException()
        }

        return result
    }
}