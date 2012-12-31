package org.socketbind.vertx.rpc;

import org.vertx.java.core.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 7/15/12
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RpcObject {
    private final static Logger logger = Logger.getLogger(RpcObject.class.getName());
    private HashMap<String, Method> callableMethods = new HashMap<String, Method>();
    private Object originalObject;

    public RpcObject(Object originalObject) {
        this.originalObject = originalObject;
        scanMethods();
    }

    private void scanMethods() {
        for ( Method m : originalObject.getClass().getDeclaredMethods() ) {
            int modifiers = m.getModifiers();
            Class[] parameterTypes = m.getParameterTypes();

            if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && m.getReturnType().equals(JsonObject.class) && parameterTypes.length > 0 && RpcContext.class.isAssignableFrom(parameterTypes[0])) {
                logger.info(String.format("Callable method: %s.%s", originalObject.getClass().getName(), m.getName()));
                callableMethods.put(m.getName(), m);
            } else {
                logger.info(String.format("Skipped method: %s.%s", originalObject.getClass().getName(), m.getName()));
            }
        }
    }

    public JsonObject tryCall(String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if ( callableMethods.containsKey(methodName) ) {
            return (JsonObject) callableMethods.get(methodName).invoke(originalObject, args);
        } else {
            throw new NoSuchMethodException(methodName);
        }
    }

    public boolean hasMethod(String name) {
        return callableMethods.containsKey(name);
    }

    public Set<String> methodNames() {
        return Collections.unmodifiableSet(callableMethods.keySet());
    }
}
