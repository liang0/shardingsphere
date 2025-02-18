/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.transaction.xa.jta.connection.dialect;

import lombok.SneakyThrows;
import org.apache.shardingsphere.transaction.xa.jta.connection.XAConnectionWrapper;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * XA connection wrapper for MariaDB.
 */
public final class MariaDBXAConnectionWrapper implements XAConnectionWrapper {
    
    private static final Class<Connection> JDBC_CONNECTION_CLASS = getJDBCConnectionClass();
    
    private static final Constructor<?> XA_CONNECTION_CONSTRUCTOR = getXAConnectionConstructor();
    
    @SuppressWarnings("unchecked")
    @SneakyThrows(ReflectiveOperationException.class)
    private static Class<Connection> getJDBCConnectionClass() {
        return (Class<Connection>) Class.forName("org.mariadb.jdbc.MariaDbConnection");
    }
    
    @SneakyThrows(ReflectiveOperationException.class)
    private static Constructor<?> getXAConnectionConstructor() {
        return Class.forName("org.mariadb.jdbc.MariaXaConnection").getDeclaredConstructor(Class.forName("org.mariadb.jdbc.MariaDbConnection"));
    }
    
    @Override
    public XAConnection wrap(final XADataSource xaDataSource, final Connection connection) throws SQLException {
        return createXAConnection(connection.unwrap(JDBC_CONNECTION_CLASS));
    }
    
    @SneakyThrows(ReflectiveOperationException.class)
    private XAConnection createXAConnection(final Connection connection) {
        return (XAConnection) XA_CONNECTION_CONSTRUCTOR.newInstance(connection);
    }
}
