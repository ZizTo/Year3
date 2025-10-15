package com.zizto.dao;

import com.zizto.model.Service;
import com.zizto.util.JdbcConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao {

    private static final String GET_ALL_SERVICES = "SELECT * FROM Services;";
    private static final String GET_SERVICES_BY_SUBSCRIBER_ID = 
        "SELECT s.id, s.name, s.monthly_cost FROM Services s " +
        "JOIN Subscriber_Services ss ON s.id = ss.service_id " +
        "WHERE ss.subscriber_id = ?;";

    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        try (Connection conn = JdbcConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_SERVICES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("id"));
                service.setName(rs.getString("name"));
                service.setMonthlyCost(rs.getBigDecimal("monthly_cost"));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public List<Service> getServicesBySubscriberId(int subscriberId) {
        List<Service> services = new ArrayList<>();
        try (Connection conn = JdbcConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_SERVICES_BY_SUBSCRIBER_ID)) {
            ps.setInt(1, subscriberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setName(rs.getString("name"));
                    service.setMonthlyCost(rs.getBigDecimal("monthly_cost"));
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
}
