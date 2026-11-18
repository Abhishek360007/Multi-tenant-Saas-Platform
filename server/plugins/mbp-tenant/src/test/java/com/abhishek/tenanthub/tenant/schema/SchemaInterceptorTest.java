package com.abhishek.tenanthub.tenant.schema;

import com.abhishek.tenanthub.tenant.props.MultiTenantProperties;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SchemaInterceptorTest {

    private final SchemaInterceptor interceptor = new SchemaInterceptor(new MultiTenantProperties());

    @Test
    void switchesWithSetCatalogSoThePoolCanResetIt() throws Exception {
        Connection conn = mock(Connection.class);
        when(conn.getCatalog()).thenReturn("tenanthub_master");

        interceptor.switchSchema(conn, "acme");

        // setCatalog is tracked by HikariCP and undone when the connection is returned;
        // a raw "USE ..." statement was not, and leaked the tenant's database to the next borrower.
        verify(conn).setCatalog("tenanthub_acme");
        verify(conn, never()).createStatement();
    }

    @Test
    void doesNothingWhenAlreadyOnThatDatabase() throws Exception {
        Connection conn = mock(Connection.class);
        when(conn.getCatalog()).thenReturn("tenanthub_acme");

        interceptor.switchSchema(conn, "acme");

        verify(conn, never()).setCatalog(anyString());
    }
}
