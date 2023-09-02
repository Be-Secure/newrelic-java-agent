package com.nr.agent.instrumentation.r2dbc;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.newrelic.agent.introspec.DatastoreHelper;
import com.newrelic.agent.introspec.InstrumentationTestConfig;
import com.newrelic.agent.introspec.InstrumentationTestRunner;
import com.newrelic.agent.introspec.Introspector;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;

@RunWith(InstrumentationTestRunner.class)
@InstrumentationTestConfig(includePrefixes = "none")
public class MySQLNoInstrumentationTest {

    public static DB mariaDb;
    public Connection connection;

    @Before
    public void setup() throws Exception {
        String databaseName = "MySQL" + System.currentTimeMillis();
        DBConfigurationBuilder builder = DBConfigurationBuilder.newBuilder().setPort(0);
        mariaDb = DB.newEmbeddedDB(builder.build());
        mariaDb.start();
        mariaDb.createDB(databaseName);
        mariaDb.source("users.sql", "user", "password", databaseName);
        ConnectionFactory connectionFactory = ConnectionFactories.get(builder.getURL(databaseName).replace("jdbc", "r2dbc").replace("localhost", "user:password@localhost"));
        connection = Mono.from(connectionFactory.create()).block();
    }

    @AfterClass
    public static void teardown() throws Exception {
        mariaDb.stop();
    }

    @Test
    public void testBasicRequests() {
        //Given
        Introspector introspector = InstrumentationTestRunner.getIntrospector();
        DatastoreHelper helper = new DatastoreHelper("MySQL");

        //When
        R2dbcTestUtils.basicRequests(connection);

        //Then
        assertEquals(1, introspector.getFinishedTransactionCount(1000));
        assertEquals(1, introspector.getTransactionNames().size());
        String transactionName = introspector.getTransactionNames().stream().findFirst().orElse("");
        helper.assertScopedStatementMetricCount(transactionName, "INSERT", "USERS", 0);
        helper.assertScopedStatementMetricCount(transactionName, "SELECT", "USERS", 0);
        helper.assertScopedStatementMetricCount(transactionName, "UPDATE", "USERS", 0);
        helper.assertScopedStatementMetricCount(transactionName, "DELETE", "USERS", 0);
    }

    @Test
    public void testParametrizedRequests() {
        //Given
        Introspector introspector = InstrumentationTestRunner.getIntrospector();
        DatastoreHelper helper = new DatastoreHelper("MySQL");

        //When
        R2dbcTestUtils.parametrizedRequests(connection);

        //Then
        assertEquals(1, introspector.getFinishedTransactionCount(1000));
        assertEquals(1, introspector.getTransactionNames().size());
        String transactionName = introspector.getTransactionNames().stream().findFirst().orElse("");
        helper.assertScopedStatementMetricCount(transactionName, "INSERT", "USERS", 0);
        helper.assertScopedStatementMetricCount(transactionName, "SELECT", "USERS", 0);
        helper.assertScopedStatementMetricCount(transactionName, "UPDATE", "USERS", 0);
        helper.assertScopedStatementMetricCount(transactionName, "DELETE", "USERS", 0);
    }
}