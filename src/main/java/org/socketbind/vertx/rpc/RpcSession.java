package org.socketbind.vertx.rpc;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.sockjs.SockJSSocket;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 10/25/12
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class RpcSession {

    private Vertx vertxInstance;
    private SockJSSocket socket;

    public RpcSession(Vertx vertxInstance, SockJSSocket socket) {
        this.vertxInstance = vertxInstance;
        this.socket = socket;
    }

    public Vertx vertx() {
        return vertxInstance;
    }

    public SockJSSocket socket() {
        return socket;
    }
}
