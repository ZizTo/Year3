package com.zizto.dao;

import com.zizto.exception.DAOException;
import com.zizto.model.Service;
import com.zizto.util.ConnectionPool;
import com.zizto.util.JdbcConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDao.class);

    private static final String GET_ALL_SERVICES = "SELECT * FROM Services;";
    private static final String GET_SERVICES_BY_SUBSCRIBER_ID = 
        "SELECT s.id, s.name, s.monthly_cost FROM Services s " +
        "JOIN Subscriber_Services ss ON s.id = ss.service_id " +
        "WHERE ss.subscriber_id = ?;";

    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getInstance().getConnection();
            ps = conn.prepareStatement(GET_ALL_SERVICES);
            rs = ps.executeQuery();
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("id"));
                service.setName(rs.getString("name"));
                service.setMonthlyCost(rs.getBigDecimal("monthly_cost"));
                services.add(service);
            }
        } catch (SQLException e) {
            throw new DAOException("Failure during getting serivece", e);
        }
        finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
            }
            catch (SQLException e) {
                LOGGER.error("Error closing",e);
            }
            if (conn != null) ConnectionPool.getInstance().releaseConnection(conn);
        }
        return services;
    }

    public List<Service> getServicesBySubscriberId(int subscriberId) {
        List<Service> services = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JdbcConnector.getConnection();
            ps = conn.prepareStatement(GET_SERVICES_BY_SUBSCRIBER_ID);
            ps.setInt(1, subscriberId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("id"));
                service.setName(rs.getString("name"));
                service.setMonthlyCost(rs.getBigDecimal("monthly_cost"));
                services.add(service);
            }
        }
        catch (SQLException e) {
            throw new DAOException("Failure during getting by id", e);
        } 
        finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
            }
            catch (SQLException e) {
                LOGGER.error("Error closing",e);
            }
            if (conn != null) ConnectionPool.getInstance().releaseConnection(conn);
        }
        return services;
    }
}
