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

        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(req.getServletContext()).buildExchange(req, resp);
        WebContext context = new WebContext(webExchange, req.getLocale());

        String action = req.getParameter("action");
        if (action == null) {
            action = "home";
        }

        try {
            switch (action) {
                case "register":
                    templateEngine.process("register", context, resp.getWriter());
                    break;

                case "saveUser":
                    String login = req.getParameter("login");
                    String pass = req.getParameter("password");
                    User newUser = new User(login, pass, Role.USER);
                    userDao.save(newUser);
                    
                    context.setVariable("message", "Registration successful! Please login.");
                    templateEngine.process("home", context, resp.getWriter());
                    break;
                
                case "loginPage":
                    templateEngine.process("login", context, resp.getWriter());
                    break;

                case "login":
                    String loginInput = req.getParameter("login");
                    String passInput = req.getParameter("password");

                    User foundUser = userDao.findByLogin(loginInput);

                    if (foundUser != null && foundUser.getPassword().equals(passInput)) {
                        HttpSession session = req.getSession();
                        session.setAttribute("currentUser", foundUser);

                        resp.sendRedirect(req.getContextPath() + "/?action=home");
                    } else {
                        context.setVariable("errorMessage", "Invalid login or password");
                        templateEngine.process("login", context, resp.getWriter());
                    }
                    break;
                
                case "blockSubscriberPage":
                    templateEngine.process("blockSubscriber", context, resp.getWriter());
                    break;

                case "blockSubscriber":
                    try {
                        int subIdToBlock = Integer.parseInt(req.getParameter("subscriberId"));

                        com.zizto.dao.SubscriberDao subscriberDao = new com.zizto.dao.SubscriberDao();
                        subscriberDao.blockSubscriber(subIdToBlock);

                        context.setVariable("message", "Subscriber ID " + subIdToBlock + " has been blocked.");
                    } catch (Exception e) {
                        context.setVariable("errorMessage", "Error blocking subscriber: " + e.getMessage());
                    }
                    templateEngine.process("home", context, resp.getWriter());
                    break;

                case "logout":
                    req.getSession().invalidate();
                    resp.sendRedirect(req.getContextPath() + "/?action=home");
                    break;
                
                case "getUnpaidBill":
                    try {
                        String subIdStr = req.getParameter("subscriberId");
                        if (subIdStr != null && !subIdStr.isEmpty()) {
                            int subId = Integer.parseInt(subIdStr);
                            Bill foundBill = billDao.getUnpaidBillBySubscriberId(subId);

                            context.setVariable("bill", foundBill);
                            context.setVariable("subscriberId", subId);
                        }
                    } catch (Exception e) {
                        context.setVariable("errorMessage", "Ошибка поиска: " + e.getMessage());
                    }
                    templateEngine.process("unpaidBillResult", context, resp.getWriter());
                    break;
                
                
                case "payBill":
                    try {
                        String billIdStr = req.getParameter("billId");
                        if (billIdStr != null) {
                            int billId = Integer.parseInt(billIdStr);

                            billDao.payBill(billId);

                            context.setVariable("message", "Счет №" + billId + " успешно оплачен!");
                        }
                    } catch (Exception e) {
                        context.setVariable("errorMessage", "Ошибка оплаты: " + e.getMessage());
                    }
                    templateEngine.process("home", context, resp.getWriter());
                    break;

                    
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
    }

    @Override
    public void destroy() {}
}
