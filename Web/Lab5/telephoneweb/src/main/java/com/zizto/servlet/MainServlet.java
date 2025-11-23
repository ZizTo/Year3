package com.zizto.servlet;

import com.zizto.controller.IController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


public class MainServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainServlet.class);
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        LOGGER.info("Инициализация MainServlet...");

        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(getServletContext());
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);

        LOGGER.info("MainServlet инициализирован.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        try {
            handleSessionAndCookies(request, response, context);

            IController controller = ControllerMappings.resolve(request);

            if (controller != null) {
                controller.process(request, response, context);

                if (request.getMethod().equalsIgnoreCase("GET") && !response.isCommitted()) {
                    String templateName = resolveTemplateName(request.getPathInfo());
                    renderTemplate(templateName, context, response);
                }
            } else {
                LOGGER.warn("Контроллер не найден для запроса: {} {}", request.getMethod(), request.getRequestURI());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Запрашиваемый ресурс не найден.");
            }
        } catch (Exception e) {
            LOGGER.error("Произошла критическая ошибка при обработке запроса", e);
            handleError(e, context, response);
        }
    }

    private void handleSessionAndCookies(HttpServletRequest request, HttpServletResponse response, WebContext context) {
        HttpSession session = request.getSession(true);

        Integer visitCount = (Integer) session.getAttribute("visitCount");
        visitCount = (visitCount == null) ? 1 : visitCount + 1;
        session.setAttribute("visitCount", visitCount);

        String lastVisitTime = "первый раз";
        if (request.getCookies() != null) {
            lastVisitTime = Arrays.stream(request.getCookies())
                .filter(c -> "lastVisit".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(lastVisitTime);
        }
        
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        String currentVisitTime = LocalDateTime.now().format(formatter);
        Cookie lastVisitCookie = new Cookie("lastVisit", currentVisitTime);
        lastVisitCookie.setMaxAge(60 * 60 * 24 * 365);
        lastVisitCookie.setPath(request.getContextPath());
        response.addCookie(lastVisitCookie);

        context.setVariable("visitCount", visitCount);
        context.setVariable("lastVisit", lastVisitTime.replace("_", " "));
    }

    private String resolveTemplateName(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/index")) {
            return "subscribers/list";
        }
        return pathInfo.substring(1);
    }

    private void renderTemplate(String templateName, WebContext context, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        templateEngine.process(templateName, context, response.getWriter());
    }

    private void handleError(Exception e, WebContext context, HttpServletResponse response) throws IOException {
        context.setVariable("errorTitle", "Произошла ошибка");
        context.setVariable("errorMessage", e.getMessage());
        context.setVariable("errorType", e.getClass().getSimpleName());
        
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        renderTemplate("error", context, response);
    }
}
