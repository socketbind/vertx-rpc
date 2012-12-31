package org.socketbind.vertx.rpc;

import junit.framework.Assert;
import org.junit.Test;
import org.socketbind.vertx.rpc.example.EndpointImpl;
import org.socketbind.vertx.rpc.example.EndpointInterface;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: atlas
 * Date: 7/15/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */

public class MethodInvokerImplTest {

    @Test
    public void testMethodCanBeCalled() {
        EndpointInterface exampleObject = new EndpointImpl();

        MethodInvoker methodInvoker = new MethodInvokerImpl();
        methodInvoker.registerObject("endpoint", exampleObject);

        try {
            JsonObject result = methodInvoker.invokeTarget("endpoint.sum", null, 1, 2);
            assertEquals(3, result.getNumber("result"));
        } catch (RpcProblemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            Assert.fail("There shouldn't be an exception");
        }
    }

    @Test
    public void testMethodCalledWithWrongNumberOfArguments() {
        EndpointInterface exampleObject = new EndpointImpl();

        MethodInvoker methodInvoker = new MethodInvokerImpl();
        methodInvoker.registerObject("endpoint", exampleObject);

        try {
            JsonObject result = methodInvoker.invokeTarget("endpoint.sum", null, 1, 2, 3);
        } catch (RpcProblemException e) {
            assertEquals(IllegalArgumentException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testNoSuchMethod() {
        EndpointInterface exampleObject = new EndpointImpl();

        MethodInvoker methodInvoker = new MethodInvokerImpl();
        methodInvoker.registerObject("endpoint", exampleObject);

        try {
            JsonObject result = methodInvoker.invokeTarget("endpoint.invalidmethod", null, 1, 2, 3);
        } catch (RpcProblemException e) {
            assertEquals(NoSuchMethodException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testNoSuchTarget() {
        MethodInvoker methodInvoker = new MethodInvokerImpl();

        try {
            JsonObject result = methodInvoker.invokeTarget("endpoint.sum", null, 1, 2);
        } catch (RpcProblemException e) {
            assertEquals(RpcProblemException.class, e.getClass());
            assertEquals("No such invocation target: endpoint", e.getMessage());
        }
    }

    @Test
    public void testTargetListIsCorrect() {
        EndpointInterface exampleObject = new EndpointImpl();

        MethodInvoker methodInvoker = new MethodInvokerImpl();
        methodInvoker.registerObject("endpoint", exampleObject);


        List<String> callableTargets = methodInvoker.callableTargets();

        for (String callableTarget : callableTargets) {
            System.out.println(callableTarget);
        }
        
        assertTrue( callableTargets.containsAll( Arrays.asList("endpoint.sum", "endpoint.diff", "system.listMethods") ) );

        try {
            JsonArray result = methodInvoker.invokeTarget("system.listMethods", new Object[] {null}).getArray("methods");
            assertTrue(Arrays.asList(result.toArray()).containsAll(Arrays.asList("endpoint.sum", "endpoint.diff", "system.listMethods")));
        } catch (RpcProblemException e) {
            Assert.fail("No exception should be reported: " + e.getMessage());
        }
    }

}
