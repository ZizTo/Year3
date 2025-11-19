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

/**
 * Логический контроллер для управления операциями, связанными с абонентами.
 * Выполняет запросы к DAO и подготавливает данные для отображения в шаблонах.
 */
public class SubscriberController {

    private final SubscriberDao subscriberDao = new SubscriberDao();
    private final ServiceDao serviceDao = new ServiceDao();
    private final BillDao billDao = new BillDao();

    /**
     * Отображает список всех абонентов.
     */
    public void listSubscribers(WebContext context) {
        context.setVariable("subscribers", subscriberDao.findAll());
        context.setVariable("pageTitle", "Список абонентов");
    }

    /**
     * Отображает детальную информацию об одном абоненте, его услугах и неоплаченном счете.
     */
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
    
    /**
     * Обрабатывает POST-запрос на блокировку абонента.
     */
    public void blockSubscriber(HttpServletRequest request, HttpServletResponse response) throws IOException, DaoException {
        try {
            int subscriberId = Integer.parseInt(request.getParameter("subscriberId"));
            subscriberDao.blockSubscriber(subscriberId);
            // После успешной блокировки перенаправляем обратно на страницу деталей
            response.sendRedirect(request.getContextPath() + "/app/subscribers/details?id=" + subscriberId);
        } catch (NumberFormatException e) {
            throw new DaoException("Некорректный ID абонента для блокировки.", e);
        }
    }
    
    /**
     * Обрабатывает POST-запрос на оплату счета.
     */
    public void payBill(HttpServletRequest request, HttpServletResponse response) throws IOException, DaoException {
        try {
            int billId = Integer.parseInt(request.getParameter("billId"));
            int subscriberId = Integer.parseInt(request.getParameter("subscriberId"));
            billDao.payBill(billId);
            // После успешной оплаты перенаправляем обратно на страницу деталей
            response.sendRedirect(request.getContextPath() + "/app/subscribers/details?id=" + subscriberId);
        } catch (NumberFormatException e) {
            throw new DaoException("Некорректный ID счета или абонента для оплаты.", e);
        }
    }
}
