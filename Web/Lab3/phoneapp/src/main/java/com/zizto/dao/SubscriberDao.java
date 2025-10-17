package com.zizto.dao;

import com.zizto.exception.DAOException;
import com.zizto.util.ConnectionPool;
import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriberDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberDao.class);

    private static final String BLOCK_SUBSCRIBER_BY_ID = "UPDATE Subscribers SET is_blocked = 1 WHERE id = ?;";

    public void blockSubscriber(int subscriberId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConnectionPool.getInstance().getConnection();
            ps = conn.prepareStatement(BLOCK_SUBSCRIBER_BY_ID);
            ps.setInt(1, subscriberId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failure during blocking", e);
        }
        finally {
            try {
                if (ps != null) ps.close();
            }
            catch (SQLException e) {
               LOGGER.error("Error closing",e);
            }
            if (conn != null) ConnectionPool.getInstance().releaseConnection(conn);
        }
    }
}
