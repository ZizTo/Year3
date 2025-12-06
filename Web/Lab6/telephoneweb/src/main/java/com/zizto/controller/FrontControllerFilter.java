package com.zizto.controller;

import com.zizto.dao.BillDao;
import com.zizto.dao.ServiceDao;
import com.zizto.dao.UserDao;
import com.zizto.model.Bill;
import com.zizto.model.Role;
import com.zizto.model.User;
import com.zizto.view.ThymeleafConfig;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

public class FrontControllerFilter implements Filter {

    private ServiceDao serviceDao;
    private BillDao billDao;
    private UserDao userDao;
    private TemplateEngine templateEngine;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация ресурсов при старте
        this.serviceDao = new ServiceDao();
        this.billDao = new BillDao();
        this.userDao = new UserDao();
        this.templateEngine = ThymeleafConfig.getTemplateEngine(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        // Подготовка контекста Thymeleaf
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(req.getServletContext()).buildExchange(req, resp);
        WebContext context = new WebContext(webExchange, req.getLocale());

        // Получаем команду из параметра action (например, ?action=register)
        String action = req.getParameter("action");
        if (action == null) {
            action = "home";
        }

        try {
            switch (action) {
                // --- КОМАНДЫ РЕГИСТРАЦИИ (НОВОЕ) ---
                case "register":
                    // Показать форму регистрации
                    templateEngine.process("register", context, resp.getWriter());
                    break;

                case "saveUser":
                    // Обработка данных формы регистрации
                    String login = req.getParameter("login");
                    String pass = req.getParameter("password");
                    // Для простоты пока регистрируем всех как USER
                    User newUser = new User(login, pass, Role.USER);
                    userDao.save(newUser);
                    
                    // После успеха отправляем на главную с сообщением
                    context.setVariable("message", "Registration successful! Please login.");
                    templateEngine.process("home", context, resp.getWriter());
                    break;
                
                                case "loginPage":
                    templateEngine.process("login", context, resp.getWriter());
                    break;

                // Обработка данных входа
                case "login":
                    String loginInput = req.getParameter("login");
                    String passInput = req.getParameter("password");

                    User foundUser = userDao.findByLogin(loginInput);

                    // Простая проверка (в реальности нужны хэши!)
                    if (foundUser != null && foundUser.getPassword().equals(passInput)) {
                        // УСПЕХ: Сохраняем пользователя в сессию
                        HttpSession session = req.getSession();
                        session.setAttribute("currentUser", foundUser);

                        // Перенаправляем на главную
                        resp.sendRedirect(req.getContextPath() + "/?action=home");
                    } else {
                        // ОШИБКА: Возвращаем на логин с сообщением
                        context.setVariable("errorMessage", "Invalid login or password");
                        templateEngine.process("login", context, resp.getWriter());
                    }
                    break;
                
                case "blockSubscriberPage":
                    templateEngine.process("blockSubscriber", context, resp.getWriter());
                    break;

                // 2. Выполнить блокировку (когда нажали кнопку на форме)
                case "blockSubscriber":
                    // Сначала проверим, что это делает Админ (на всякий случай, хотя SecurityFilter это тоже делает)
                    // Но так как у нас есть SecurityFilter, тут можно сразу к делу:

                    try {
                        int subIdToBlock = Integer.parseInt(req.getParameter("subscriberId"));
                        // Нам нужен SubscriberDao (нужно добавить поле subscriberDao в класс, если его нет)
                        // Но чтобы не усложнять, можно использовать serviceDao или создать SubscriberDao тут
                        com.zizto.dao.SubscriberDao subscriberDao = new com.zizto.dao.SubscriberDao();
                        subscriberDao.blockSubscriber(subIdToBlock);

                        context.setVariable("message", "Subscriber ID " + subIdToBlock + " has been blocked.");
                    } catch (Exception e) {
                        context.setVariable("errorMessage", "Error blocking subscriber: " + e.getMessage());
                    }
                    // Возвращаемся на главную
                    templateEngine.process("home", context, resp.getWriter());
                    break;

                // Выход из системы
                case "logout":
                    req.getSession().invalidate(); // Удаляем сессию
                    resp.sendRedirect(req.getContextPath() + "/?action=home");
                    break;
                
                case "getUnpaidBill":
                    try {
                        // 1. Получаем ID абонента из формы
                        String subIdStr = req.getParameter("subscriberId");
                        if (subIdStr != null && !subIdStr.isEmpty()) {
                            int subId = Integer.parseInt(subIdStr);
                        
                            // 2. Обращаемся к DAO (который мы только что исправили)
                            Bill foundBill = billDao.getUnpaidBillBySubscriberId(subId);
                        
                            // 3. Передаем найденный счет (или null) в HTML
                            context.setVariable("bill", foundBill);
                            context.setVariable("subscriberId", subId);
                        }
                    } catch (Exception e) {
                        context.setVariable("errorMessage", "Ошибка поиска: " + e.getMessage());
                    }
                    // 4. Открываем страницу результата
                    templateEngine.process("unpaidBillResult", context, resp.getWriter());
                    break;
                
                
                // КОМАНДА 2: ОПЛАТА СЧЕТА (Кнопка "Pay Bill")
                case "payBill":
                    try {
                        String billIdStr = req.getParameter("billId");
                        if (billIdStr != null) {
                            int billId = Integer.parseInt(billIdStr);

                            // Вызываем метод оплаты в DAO
                            billDao.payBill(billId);

                            context.setVariable("message", "Счет №" + billId + " успешно оплачен!");
                        }
                    } catch (Exception e) {
                        context.setVariable("errorMessage", "Ошибка оплаты: " + e.getMessage());
                    }
                    // Возвращаем на главную
                    templateEngine.process("home", context, resp.getWriter());
                    break;

                    
                // --- СТАРЫЕ КОМАНДЫ ---s
                case "listServices":
                    context.setVariable("services", serviceDao.getAllServices());
                    templateEngine.process("services", context, resp.getWriter());
                    break;

                case "showUnpaidBillForm":
                    templateEngine.process("unpaidBillForm", context, resp.getWriter());
                    break;

                case "home":
                default:
                    templateEngine.process("home", context, resp.getWriter());
                    break;
            }
        } catch (Exception e) {
            context.setVariable("errorMessage", e.getMessage());
            templateEngine.process("error", context, resp.getWriter());
        }
        // ВНИМАНИЕ: Мы НЕ вызываем chain.doFilter, так как этот фильтр сам генерирует ответ (является контроллером).
    }

    @Override
    public void destroy() {}
}
