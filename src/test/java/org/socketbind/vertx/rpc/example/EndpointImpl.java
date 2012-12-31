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
public class EndpointImpl implements EndpointInterface {
    @Override
    public JsonObject sum(RpcContext context, int a, int b) {
        return new JsonObject().putNumber("result", a+b);
    }

    @Override
    public JsonObject diff(RpcContext context, int a, int b) {
        return new JsonObject().putNumber("result", a-b);
    }

    @Override
    public JsonObject multiply(RpcContext context, int a, int b) {
        return new JsonObject().putNumber("result", a*b);
    }
}
