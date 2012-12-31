package org.socketbind.vertx.rpc;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 7/15/12
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RpcProblemException extends Exception {
    public RpcProblemException() {
    }

    public RpcProblemException(String message) {
        super(message);
    }

    public RpcProblemException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcProblemException(Throwable cause) {
        super(cause);
    }
}
