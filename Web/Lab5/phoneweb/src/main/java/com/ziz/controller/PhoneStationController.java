package com.ziz.controller;

import com.ziz.dao.BillDao;
import com.ziz.dao.ServiceDao;
import com.ziz.dao.SubscriberDao;
import com.ziz.model.Bill;
import com.ziz.model.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneStationController {
    private final ServiceDao serviceDao;
    private final SubscriberDao subscriberDao;
    private final BillDao billDao;

    public PhoneStationController() {
        this.serviceDao = new ServiceDao();
        this.subscriberDao = new SubscriberDao();
        this.billDao = new BillDao();
    }

    public Map<String, Object> listAllServices() {
        Map<String, Object> context = new HashMap<>();
        List<Service> services = serviceDao.getAllServices();
        context.put("services", services);
        context.put("title", "All Available Services");
        return context;
    }

    public Map<String, Object> getSubscriberInfo(int subscriberId) {
        Map<String, Object> context = new HashMap<>();
        List<Service> services = serviceDao.getServicesBySubscriberId(subscriberId);
        Bill unpaidBill = billDao.getUnpaidBillBySubscriberId(subscriberId);

        context.put("subscriberId", subscriberId);
        context.put("services", services);
        context.put("unpaidBill", unpaidBill);
        context.put("title", "Information for Subscriber ID: " + subscriberId);
        return context;
    }



    public Map<String, Object> payBill(int billId) {
        Map<String, Object> context = new HashMap<>();
        billDao.payBill(billId);
        context.put("message", "Bill with ID " + billId + " has been successfully paid.");
        context.put("title", "Payment Confirmation");
        return context;
    }

    public Map<String, Object> blockSubscriber(int subscriberId) {
        Map<String, Object> context = new HashMap<>();
        subscriberDao.blockSubscriber(subscriberId);
        context.put("message", "Subscriber with ID " + subscriberId + " has been blocked.");
        context.put("title", "Block Confirmation");
        return context;
    }
}
