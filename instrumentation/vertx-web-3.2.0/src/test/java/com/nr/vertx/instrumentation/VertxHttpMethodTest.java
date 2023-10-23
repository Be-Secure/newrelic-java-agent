/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.nr.vertx.instrumentation;

import com.newrelic.agent.introspec.InstrumentationTestConfig;
import com.newrelic.agent.introspec.InstrumentationTestRunner;
import com.newrelic.agent.introspec.Introspector;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static com.nr.vertx.test.handlers.SimpleHandlers.createHandler;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = { "io.vertx" })
public class VertxHttpMethodTest extends VertxTestBase {

    @Test
    public void testGet() {
        Vertx vertx = Vertx.vertx();
        try {

            Router router = Router.router(vertx);
            router.get("/my/path").handler(createHandler("ResponseHandler", true));
            int port = getAvailablePort();
            HttpServer server = createServer(vertx, router, port);

            getRequest("/my/path", server, port)
                    .then().body(containsString("ResponseHandler sent response"));
            Map<String, Object> attributes = getAttributesForTransaction("OtherTransaction/Vertx/my/path (GET)");
            assertNotNull(attributes.get("ResponseHandler"));
        } finally {
            vertx.close();
        }
    }

    @Test
    public void testPost() {
        Vertx vertx = Vertx.vertx();
        try {

            Router router = Router.router(vertx);
            router.post("/my/path").handler(createHandler("ResponseHandler", true));
            int port = getAvailablePort();
            HttpServer server = createServer(vertx, router, port);

            postRequest("/my/path", server, port)
                    .then().body(containsString("ResponseHandler sent response"));
            Map<String, Object> attributes = getAttributesForTransaction("OtherTransaction/Vertx/my/path (POST)");
            assertNotNull(attributes.get("ResponseHandler"));
        } finally {
            vertx.close();
        }
    }

    @Test
    public void testPut() {
        Vertx vertx = Vertx.vertx();
        try {

            Router router = Router.router(vertx);
            router.put("/my/path").handler(createHandler("ResponseHandler", true));
            int port = getAvailablePort();
            HttpServer server = createServer(vertx, router, port);

            putRequest("/my/path", server, port)
                    .then().body(containsString("ResponseHandler sent response"));
            Map<String, Object> attributes = getAttributesForTransaction("OtherTransaction/Vertx/my/path (PUT)");
            assertNotNull(attributes.get("ResponseHandler"));
        } finally {
            vertx.close();
        }
    }

    @Test
    public void testDelete() {
        Vertx vertx = Vertx.vertx();
        try {

            Router router = Router.router(vertx);
            router.delete("/my/path").handler(createHandler("ResponseHandler", true));
            int port = getAvailablePort();
            HttpServer server = createServer(vertx, router, port);

            deleteRequest("/my/path", server, port)
                    .then().body(containsString("ResponseHandler sent response"));
            Map<String, Object> attributes = getAttributesForTransaction("OtherTransaction/Vertx/my/path (DELETE)");
            assertNotNull(attributes.get("ResponseHandler"));
        } finally {
            vertx.close();
        }
    }

    @Test
    public void testHead() {
        Vertx vertx = Vertx.vertx();
        try {

            Router router = Router.router(vertx);
            router.head("/my/path").handler(createHandler("ResponseHandler", true));
            int port = getAvailablePort();
            HttpServer server = createServer(vertx, router, port);

            headRequest("/my/path", server, port).then().statusCode(200);
            Map<String, Object> attributes = getAttributesForTransaction("OtherTransaction/Vertx/my/path (HEAD)");
            assertNotNull(attributes.get("ResponseHandler"));
        } finally {
            vertx.close();
        }
    }

    @Test
    public void testUnnamed() {
        Vertx vertx = Vertx.vertx();
        try {

            Router router = Router.router(vertx);
            router.route().handler(createHandler("ResponseHandler", true));
            int port = getAvailablePort();
            HttpServer server = createServer(vertx, router, port);

            final Introspector introspector = InstrumentationTestRunner.getIntrospector();

            getRequest("", server, port).then().body(containsString("ResponseHandler sent response"));
            assertEquals(1, introspector.getFinishedTransactionCount(TIMEOUT));
            assertTrue(introspector.getTransactionNames().contains("OtherTransaction/Vertx/UnnamedPath (GET)"));

            postRequest("", server, port).then().body(containsString("ResponseHandler sent response"));
            assertEquals(2, introspector.getFinishedTransactionCount(TIMEOUT));
            assertTrue(introspector.getTransactionNames().contains("OtherTransaction/Vertx/UnnamedPath (POST)"));

            deleteRequest("", server, port).then().body(containsString("ResponseHandler sent response"));
            assertEquals(3, introspector.getFinishedTransactionCount(TIMEOUT));
            assertTrue(introspector.getTransactionNames().contains("OtherTransaction/Vertx/UnnamedPath (DELETE)"));

            putRequest("", server, port).then().body(containsString("ResponseHandler sent response"));
            assertEquals(4, introspector.getFinishedTransactionCount(TIMEOUT));
            assertTrue(introspector.getTransactionNames().contains("OtherTransaction/Vertx/UnnamedPath (PUT)"));

            headRequest("", server, port).then().statusCode(200);
            assertEquals(5, introspector.getFinishedTransactionCount(TIMEOUT));
            assertTrue(introspector.getTransactionNames().contains("OtherTransaction/Vertx/UnnamedPath (HEAD)"));
        } finally {
            vertx.close();
        }
    }

}