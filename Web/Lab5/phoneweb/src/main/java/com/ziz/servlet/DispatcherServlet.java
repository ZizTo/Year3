package com.ziz.servlet;

import com.ziz.controller.PhoneStationController;
import com.ziz.util.ThymeleafUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet("/app")
public class DispatcherServlet extends HttpServlet {
    private PhoneStationController controller;

    @Override
    public void init() throws ServletException {
        this.controller = new PhoneStationController();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");

        // Session and Cookie Management
        handleSessionAndCookies(req, resp);
        
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext context = new WebContext(webExchange, req.getLocale());
        
        String action = req.getParameter("action");
        if (action == null) {
            action = "home";
        }
        
        String templateName = "index";
        Map<String, Object> data = null;

        try {
            switch (action) {
                case "listAllServices":
                    data = controller.listAllServices();
                    templateName = "results";
                    break;
                case "getSubscriberInfo":
                    int subId = Integer.parseInt(req.getParameter("subscriberId"));
                    data = controller.getSubscriberInfo(subId);
                    templateName = "subscriber-info";
                    break;
                case "payBill":
                    int billId = Integer.parseInt(req.getParameter("billId"));
                    data = controller.payBill(billId);
                    templateName = "results";
                    break;
                case "blockSubscriber":
                    int subIdToBlock = Integer.parseInt(req.getParameter("subscriberId"));
                    data = controller.blockSubscriber(subIdToBlock);
                    templateName = "results";
                    break;
                case "home":
                default:
                    templateName = "index";
                    break;
            }

            if (data != null) {
                context.setVariables(data);
            }

        } catch (NumberFormatException e) {
            context.setVariable("error", "Invalid ID format. Please enter a number.");
            templateName = "index";
        } catch (Exception e) {
            context.setVariable("error", "An unexpected error occurred: " + e.getMessage());
            templateName = "index";
            e.printStackTrace();
        }
        
        ThymeleafUtil.getTemplateEngine().process(templateName, context, resp.getWriter());
    }

    private void handleSessionAndCookies(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(true); // Create session if it doesn't exist

        // Visit Count
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) {
            visitCount = 1;
        } else {
            visitCount++;
        }
        session.setAttribute("visitCount", visitCount);

        // Last Visit Time
        String lastVisitTime = "This is your first visit.";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastVisit".equals(cookie.getName())) {
                    lastVisitTime = cookie.getValue();
                }
            }
        }
        
        // Set new cookie for this visit
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Cookie lastVisitCookie = new Cookie("lastVisit", currentDateTime.replace(" ", "_")); // Cookie values cannot have spaces
        lastVisitCookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
        resp.addCookie(lastVisitCookie);
        
        // Add info to request for Thymeleaf
        req.setAttribute("visitCount", visitCount);
        req.setAttribute("lastVisitTime", lastVisitTime.replace("_", " "));
    }
}
