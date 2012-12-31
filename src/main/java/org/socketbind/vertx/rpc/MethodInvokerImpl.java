package org.socketbind.vertx.rpc;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 7/15/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodInvokerImpl implements MethodInvoker {
    private HashMap<String, RpcObject> rpcObjects = new HashMap<String, RpcObject>();
    private final static Pattern TARGET_PATTERN = Pattern.compile("^([A-Za-z]+)\\.([A-Za-z]+)$");

    public MethodInvokerImpl() {
        registerObject("system", new SystemNamespace());
    }

    @Override
    public void registerObject(String withName, Object object) {
        rpcObjects.put(withName, new RpcObject(object));
    }

    @Override
    public JsonObject invokeTarget(String target, Object... args) throws RpcProblemException {
        Matcher targetMatcher = TARGET_PATTERN.matcher(target);

        if (!targetMatcher.matches()) {
            throw new RpcProblemException("Not a valid target: " + target);
        }

        String targetObject = targetMatcher.group(1);
        String targetMethod = targetMatcher.group(2);

        if ( rpcObjects.containsKey(targetObject) ) {
            RpcObject rpc = rpcObjects.get(targetObject);

            try {
                return rpc.tryCall(targetMethod, args);
            } catch (Exception e) {
                throw new RpcProblemException(e);
            }
        } else {
            throw new RpcProblemException("No such invocation target: " + targetObject);
        }
    }

    public List<String> callableTargets() {
        List<String> targets = new ArrayList<String>();

        for (Map.Entry<String, RpcObject> entries : rpcObjects.entrySet() ) {
            String targetObject = entries.getKey();

            for ( String name : entries.getValue().methodNames() ) {
                targets.add( targetObject + "." + name );
            }
        }

        return targets;
    }

    private class SystemNamespace {
        public JsonObject listMethods(RpcContext context) {
            return new JsonObject().putArray("methods", new JsonArray( callableTargets().toArray() ));
        }
    }
}