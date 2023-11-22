package org.eclipse.jetty.ee10.servlet;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;

@Weave(originalName = "org.eclipse.jetty.ee10.servlet.ServletContextHandler", type = MatchType.ExactClass)
public class ServletContextHandler_Instrumentation {

    protected void requestDestroyed(Request baseRequest, HttpServletRequest request) {
        NewRelic.getAgent().getTransaction().addOutboundResponseHeaders();
        Weaver.callOriginal();
    }
}
