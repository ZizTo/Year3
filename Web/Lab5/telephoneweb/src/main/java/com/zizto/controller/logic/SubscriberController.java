package com.zizto.controller.logic;

import com.zizto.dao.BillDao;
import com.zizto.dao.DaoException;
import com.zizto.dao.ServiceDao;
import com.zizto.dao.SubscriberDao;
import com.zizto.model.Bill;
import com.zizto.model.Service;
import com.zizto.model.Subscriber;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.util.List;


public class SubscriberController {

    private final SubscriberDao subscriberDao = new SubscriberDao();
    private final ServiceDao serviceDao = new ServiceDao();
    private final BillDao billDao = new BillDao();

    public void listSubscribers(WebContext context) {
        context.setVariable("subscribers", subscriberDao.findAll());
        context.setVariable("pageTitle", "Список абонентов");
    }

    public void showSubscriberDetails(HttpServletRequest request, WebContext context) {
        try {
            int subscriberId = Integer.parseInt(request.getParameter("id"));
            Subscriber subscriber = subscriberDao.findById(subscriberId);

            if (subscriber == null) {
                context.setVariable("error", "Абонент с ID " + subscriberId + " не найден.");
                return;
            }

            List<Service> services = serviceDao.getServicesBySubscriberId(subscriberId);
            Bill unpaidBill = billDao.getUnpaidBillBySubscriberId(subscriberId);

            context.setVariable("subscriber", subscriber);
            context.setVariable("services", services);
            context.setVariable("unpaidBill", unpaidBill);
            context.setVariable("pageTitle", "Детали абонента: " + subscriber.getFullName());

        } catch (NumberFormatException e) {
            context.setVariable("error", "Некорректный ID абонента.");
        } catch (DaoException e) {
            context.setVariable("error", "Ошибка при получении данных: " + e.getMessage());
        }
    }

    public void blockSubscriber(HttpServletRequest request, HttpServletResponse response) throws IOException, DaoException {
        try {
            int subscriberId = Integer.parseInt(request.getParameter("subscriberId"));
            subscriberDao.blockSubscriber(subscriberId);
            response.sendRedirect(request.getContextPath() + "/app/subscribers/details?id=" + subscriberId);
        } catch (NumberFormatException e) {
            throw new DaoException("Некорректный ID абонента для блокировки.", e);
        }
    }
    

    public void payBill(HttpServletRequest request, HttpServletResponse response) throws IOException, DaoException {
        try {
            int billId = Integer.parseInt(request.getParameter("billId"));
            int subscriberId = Integer.parseInt(request.getParameter("subscriberId"));
            billDao.payBill(billId);
            response.sendRedirect(request.getContextPath() + "/app/subscribers/details?id=" + subscriberId);
        } catch (NumberFormatException e) {
            throw new DaoException("Некорректный ID счета или абонента для оплаты.", e);
        }
    }
}
