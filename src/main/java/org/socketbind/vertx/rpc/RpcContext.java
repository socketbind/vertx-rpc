package org.socketbind.vertx.rpc;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 9/15/12
 * Time: 11:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class RpcContext {

    protected String clientId;
    protected Vertx vertx;

    public RpcContext(RpcSession session) {
        this.vertx = session.vertx();
        this.clientId = session.socket().writeHandlerID;
    }

    public String getClientId() {
        return clientId;
    }

    public void notification(List<String> clients, String method, JsonObject params) {
        JsonObject message = wrapNotification(method, params);
        Buffer buffer = new Buffer(message.encode());

        for ( String client : clients ) {
            vertx.eventBus().send(client, buffer);
        }
    }

    public void notification(String client, String method, JsonObject params) {
        JsonObject message = wrapNotification(method, params);
        Buffer buffer = new Buffer(message.encode());

        vertx.eventBus().send(client, buffer);
    }

    private JsonObject wrapNotification(String method, JsonObject params) {
        JsonObject message = new JsonObject();
        message.putString("method", method);
        message.putObject("params", params);
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RpcContext that = (RpcContext) o;

        if (!clientId.equals(that.clientId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return clientId.hashCode();
    }
}

