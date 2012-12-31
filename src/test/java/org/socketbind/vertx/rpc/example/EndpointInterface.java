package org.socketbind.vertx.rpc.example;

import org.socketbind.vertx.rpc.RpcContext;
import org.vertx.java.core.json.JsonObject;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 7/15/12
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EndpointInterface {
    public JsonObject sum(RpcContext context, int a, int b);
    public JsonObject diff(RpcContext context, int a, int b);
    public JsonObject multiply(RpcContext context, int a, int b);
}
