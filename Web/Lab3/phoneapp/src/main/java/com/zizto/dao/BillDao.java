package com.zizto.dao;

import com.zizto.model.Bill;
import com.zizto.util.JdbcConnector;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class BillDao {

    private static final String GET_UNPAID_BILL_BY_SUBSCRIBER_ID = "SELECT * FROM Bills WHERE subscriber_id = ? AND is_paid = 0;";
    private static final String PAY_BILL_BY_ID = "UPDATE Bills SET is_paid = 1 WHERE id = ?;";

    public List<Bill> getUnpaidBillBySubscriberId(int subscriberId) {
        List<Bill> bills = new ArrayList<Bill>();
        try (Connection conn = JdbcConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_UNPAID_BILL_BY_SUBSCRIBER_ID)) {
            ps.setInt(1, subscriberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bill nbill = new Bill();
                    nbill.setId(rs.getInt("id"));
                    nbill.setSubscriberId(rs.getInt("subscriber_id"));
                    nbill.setAmount(rs.getBigDecimal("amount"));
                    nbill.setIssueDate(rs.getDate("issue_date"));
                    nbill.setPaid(rs.getBoolean("is_paid"));
                    bills.add(nbill);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bills;
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
