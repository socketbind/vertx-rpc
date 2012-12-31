package org.socketbind.vertx.rpc;

import org.vertx.java.core.json.JsonObject;

import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: atlas
* Date: 7/15/12
* Time: 3:07 PM
* To change this template use File | Settings | File Templates.
*/
public interface MethodInvoker {
    public void registerObject(String withName, Object object);
    public JsonObject invokeTarget(String target, Object... args) throws RpcProblemException;
    public List<String> callableTargets();
}
