package com.nr.instrumentation.jakarta.ws.rs.api;

import com.newrelic.api.agent.weaver.Weave;

@Weave(originalName = "jakarta.ws.rs.core.Application")
public class Application_Instrumentation {
    // putting this here to prevent 2.0 implementations to be weaved by this module
}