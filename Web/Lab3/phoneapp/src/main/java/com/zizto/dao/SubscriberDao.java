package com.zizto.dao;

import com.zizto.util.JdbcConnector;
import java.sql.*;

public class SubscriberDao {

    private static final String BLOCK_SUBSCRIBER_BY_ID = "UPDATE Subscribers SET is_blocked = 1 WHERE id = ?;";

    public void blockSubscriber(int subscriberId) {
        try (Connection conn = JdbcConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(BLOCK_SUBSCRIBER_BY_ID)) {
            ps.setInt(1, subscriberId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
