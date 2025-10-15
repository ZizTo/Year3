package com.zizto.dao;

import com.zizto.model.Bill;
import com.zizto.util.JdbcConnector;
import java.sql.*;

public class BillDao {

    private static final String GET_UNPAID_BILL_BY_SUBSCRIBER_ID = "SELECT * FROM Bills WHERE subscriber_id = ? AND is_paid = 0;";
    private static final String PAY_BILL_BY_ID = "UPDATE Bills SET is_paid = 1 WHERE id = ?;";

    public Bill getUnpaidBillBySubscriberId(int subscriberId) {
        Bill bill = null;
        try (Connection conn = JdbcConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_UNPAID_BILL_BY_SUBSCRIBER_ID)) {
            ps.setInt(1, subscriberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bill = new Bill();
                    bill.setId(rs.getInt("id"));
                    bill.setSubscriberId(rs.getInt("subscriber_id"));
                    bill.setAmount(rs.getBigDecimal("amount"));
                    bill.setIssueDate(rs.getDate("issue_date"));
                    bill.setPaid(rs.getBoolean("is_paid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bill;
    }

    public void payBill(int billId) {
        try (Connection conn = JdbcConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(PAY_BILL_BY_ID)) {
            ps.setInt(1, billId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
