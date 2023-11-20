package com.nr.agent.instrumentation.jetty.ee10.servlet;

import com.newrelic.agent.bridge.AgentBridge;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.ee10.servlet.AsyncContextEvent;
import org.eclipse.jetty.ee10.servlet.ServletChannelState;
import org.eclipse.jetty.ee10.servlet.ServletContextRequest;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class ServerHelper {
    private static final AtomicBoolean HAS_CONTEXT_HANDLER = new AtomicBoolean(false);

    public static boolean hasContextHandler() {
        return HAS_CONTEXT_HANDLER.get();
    }

    public static void contextHandlerFound() {
        if (!HAS_CONTEXT_HANDLER.getAndSet(true)) {
            AgentBridge.getAgent().getLogger().log(Level.FINE, "Detected Jetty ContextHandler");
        }
    }

    public static void preHandleDispatch(ServletContextRequest servletContextRequest) {
        HttpServletRequest request = servletContextRequest.getServletApiRequest();
        HttpServletResponse response = servletContextRequest.getHttpServletResponse();
        AgentBridge.getAgent().getTransaction(true).requestInitialized(new JettyRequest(request),
                new JettyResponse(response));
    }

    public static void preHandleDispatchAsync(ServletContextRequest servletContextRequest, ServletChannelState state) {
        HttpServletRequest request = servletContextRequest.getServletApiRequest();
        AsyncContextEvent asyncContextEvent = state.getAsyncContextEvent();
        if (asyncContextEvent == null) {
            AgentBridge.getAgent().getLogger().log(Level.FINE, "AsyncContextEvent is null for request: {0}.", request);
            return;
        }
        AgentBridge.asyncApi.resumeAsync(asyncContextEvent.getAsyncContext());
    }

    public static void postHandleDispatch(ServletContextRequest servletContextRequest) {
        HttpServletRequest request = servletContextRequest.getServletApiRequest();
        if (request.isAsyncStarted()) {
            AgentBridge.asyncApi.suspendAsync(request.getAsyncContext());
        }
        AgentBridge.getAgent().getTransaction().requestDestroyed();
    }

}
