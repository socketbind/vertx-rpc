package org.socketbind.vertx.rpc;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 9/15/12
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientStateAware<C extends RpcContext> {
    public void clientConnected(C context);
    public void clientError(C context, Exception e);
    public void clientDisconnected(C context);
}
