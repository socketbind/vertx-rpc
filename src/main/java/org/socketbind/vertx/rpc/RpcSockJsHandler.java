package org.socketbind.vertx.rpc;

import com.google.common.collect.ObjectArrays;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSSocket;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 9/15/12
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class RpcSockJsHandler implements Handler<SockJSSocket> {
    private final static Logger logger = Logger.getLogger(RpcSockJsHandler.class.getName());
    private List<ClientStateAware> clientStateAwareList;
    private MethodInvoker methodInvoker;
    private Vertx vertx;

    private Constructor<? extends RpcContext> contextConstructor;

    public RpcSockJsHandler(Vertx vertx) {
        this.clientStateAwareList = new ArrayList<ClientStateAware>();
        this.methodInvoker = new MethodInvokerImpl();
        this.vertx = vertx;
        this.contextConstructor = getContextConstructor(RpcContext.class);
    }

    public RpcSockJsHandler(Vertx vertx, Class<? extends RpcContext> customContextClass) {
        this.clientStateAwareList = new ArrayList<ClientStateAware>();
        this.methodInvoker = new MethodInvokerImpl();
        this.vertx = vertx;
        this.contextConstructor = getContextConstructor(customContextClass);
    }

    private Constructor<? extends RpcContext> getContextConstructor(Class<? extends RpcContext> clazz) {
        try {
            return clazz.getConstructor(RpcSession.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(clazz.getName() + " does not have a constructor that takes an RpcSession instance", e);
        }
    }

    @Override
    public void handle(final SockJSSocket socket) {
        RpcSession session = new RpcSession(vertx, socket);

        try {
            final RpcContext context = contextConstructor.newInstance(session);

            logger.finest("Incoming client: " + context.getClientId());

            socket.dataHandler(new Handler<Buffer>() {
                @Override
                public void handle(Buffer buffer) {
                    handleIncomingData(context, socket, buffer);
                }
            });

            socket.endHandler(new Handler<Void>() {
                @Override
                public void handle(Void aVoid) {

                    for (ClientStateAware aware : clientStateAwareList) {
                        aware.clientDisconnected(context);
                    }

                    logger.finest("Client disconnect: " + context.getClientId());
                }
            });

            socket.exceptionHandler(new Handler<Exception>() {
                @Override
                public void handle(Exception e) {
                    for (ClientStateAware aware : clientStateAwareList) {
                        aware.clientError(context, e);
                    }

                    logger.log(Level.SEVERE, "Exception with client " + context.getClientId(), e);
                }
            });

            for (ClientStateAware aware : clientStateAwareList) {
                aware.clientConnected(context);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception with client " + socket.writeHandlerID, e);
        }
    }

    private void handleIncomingData(final RpcContext context, final SockJSSocket socket, final Buffer buffer) {
        JsonObject message = new JsonObject(buffer.toString());

        String method = message.getString("method");
        JsonArray params = message.getArray("params");
        Number id = message.getNumber("id");

        JsonObject reply = new JsonObject();
        if (id != null) {
            reply.putNumber("id", id);
        }

        if ( method == null || params == null ) {
            reply.putString("error", "Missing method/params");
            logger.log(Level.SEVERE, "Got invalid packet with missing method or params from" + context.getClientId());
        } else {
            try {
                Object[] finalParams = ObjectArrays.concat(context, params.toArray());
                JsonObject returnVal = methodInvoker.invokeTarget(method, finalParams);
                if (returnVal != null) {
                    reply.putObject("result", returnVal);
                }
            } catch (RpcProblemException e) {
                reply.putString("method", method);
                reply.putString("error", "RPC error");
                logger.log(Level.SEVERE, "Exception while dealing with incoming data, client " + context.getClientId(), e);
            }
        }

        socket.writeBuffer(new Buffer(reply.encode()));
    }

    public void registerObject(String withName, Object object) {
        logger.finest("Registered object with name: " + withName);

        methodInvoker.registerObject(withName, object);

        if (object instanceof ClientStateAware) {
            logger.finest("Object is client state aware: " + withName);
            clientStateAwareList.add((ClientStateAware) object);
        }
    }
}
