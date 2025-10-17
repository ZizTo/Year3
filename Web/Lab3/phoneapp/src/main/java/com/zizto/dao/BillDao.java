package com.zizto.dao;

import com.zizto.exception.DAOException;
import com.zizto.model.Bill;
import com.zizto.util.ConnectionPool;
import com.zizto.util.JdbcConnector;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.sql.*;

public class BillDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillDao.class);

    private static final String GET_UNPAID_BILL_BY_SUBSCRIBER_ID = "SELECT * FROM Bills WHERE subscriber_id = ? AND is_paid = 0;";
    private static final String PAY_BILL_BY_ID = "UPDATE Bills SET is_paid = 1 WHERE id = ?;";

    public List<Bill> getUnpaidBillBySubscriberId(int subscriberId) {
        List<Bill> bills = new ArrayList<Bill>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JdbcConnector.getConnection();
            ps = conn.prepareStatement(GET_UNPAID_BILL_BY_SUBSCRIBER_ID);
            ps.setInt(1, subscriberId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Bill nbill = new Bill();
                nbill.setId(rs.getInt("id"));
                nbill.setSubscriberId(rs.getInt("subscriber_id"));
                nbill.setAmount(rs.getBigDecimal("amount"));
                nbill.setIssueDate(rs.getDate("issue_date"));
                nbill.setPaid(rs.getBoolean("is_paid"));
                bills.add(nbill);
            }
        } catch (SQLException e) {
            throw new DAOException("Failure during unpaid bills", e);
        }
        finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) ps.close();
            }
            catch (SQLException e) {
                LOGGER.error("Error closing",e);
            }
            if (conn != null) ConnectionPool.getInstance().releaseConnection(conn);
        }
        return bills;
    }

    public void payBill(int billId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JdbcConnector.getConnection();
            ps = conn.prepareStatement(PAY_BILL_BY_ID);
            ps.setInt(1, billId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failure during pay bills", e);
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
