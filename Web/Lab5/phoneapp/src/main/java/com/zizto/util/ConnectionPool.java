package com.zizto.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConnectionPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

    private final BlockingQueue<Connection> connectionQueue;
    private final String url;
    private final String user;
    private final String password;

    private static final ConnectionPool instance = new ConnectionPool();

    private ConnectionPool() {
        Properties properties = new Properties();
        try (InputStream input = ConnectionPool.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("database.properties not found");
            }
            properties.load(input);
            this.url = properties.getProperty("db.url");
            this.user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Error loading db.prp", e);
        }

        this.connectionQueue = new ArrayBlockingQueue<>(10);

        for (int i = 0; i < 10; i++) {
            try {
                Connection connection = createConnection();
                connectionQueue.offer(connection);
            } catch (SQLException e) {
                LOGGER.error("Failed to create connection", e);
            }
        }
        LOGGER.info("Connection pool initialized", connectionQueue.size());
    }
    
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static ConnectionPool getInstance() {
        return instance;
    }

    public Connection getConnection() {
        try {
            return connectionQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to get connection", e);
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connectionQueue.offer(connection);
            } catch (Exception e) {
                LOGGER.error("Error releasing connection", e);
            }
        }
    }
    
    public void shutdown() {
        LOGGER.info("Shutting down ConPool...");
        for (Connection connection : connectionQueue) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("Error closing connection", e);
            }
        }
    }
}
