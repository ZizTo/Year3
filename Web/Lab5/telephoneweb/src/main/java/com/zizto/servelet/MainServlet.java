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

/**
 * Главный сервлет-диспетчер приложения "Телефонная Станция".
 *
 * - Часть 1: Принимает все HTTP-запросы на /app/* и передает управление контроллерам
 *   через ControllerMappings.
 * - Часть 2: Управляет сессиями и cookies для каждого пользователя.
 * - Часть 2: Централизованно обрабатывает все исключения, перенаправляя на страницу error.html.
 */
public class MainServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainServlet.class);
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        LOGGER.info("Инициализация MainServlet...");

        // Инициализация Thymeleaf
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(getServletContext());
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        // Отключаем кэширование для удобства разработки
        templateResolver.setCacheable(false);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);

        LOGGER.info("MainServlet успешно инициализирован.");
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
        // Создаем WebExchange и WebContext для Thymeleaf
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response);
        WebContext context = new WebContext(webExchange, request.getLocale());

        try {
            // Обработка сессии и cookies
            handleSessionAndCookies(request, response, context);

            // Находим нужный контроллер по URL и методу запроса
            IController controller = ControllerMappings.resolve(request);

            if (controller != null) {
                // Выполняем логику контроллера
                controller.process(request, response, context);

                // Если это GET-запрос и не было редиректа, отображаем шаблон
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

    /**
     * Обрабатывает сессию и cookies в соответствии с требованиями лабораторной работы.
     */
    private void handleSessionAndCookies(HttpServletRequest request, HttpServletResponse response, WebContext context) {
        HttpSession session = request.getSession(true);

        // Счётчик посещений (в сессии)
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        visitCount = (visitCount == null) ? 1 : visitCount + 1;
        session.setAttribute("visitCount", visitCount);

        // Дата/время последнего визита (в cookie)
        String lastVisitTime = "первый раз";
        if (request.getCookies() != null) {
            lastVisitTime = Arrays.stream(request.getCookies())
                .filter(c -> "lastVisit".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(lastVisitTime);
        }
        
        // Устанавливаем новый cookie с текущим временем
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        String currentVisitTime = LocalDateTime.now().format(formatter);
        Cookie lastVisitCookie = new Cookie("lastVisit", currentVisitTime);
        lastVisitCookie.setMaxAge(60 * 60 * 24 * 365); // 1 год
        lastVisitCookie.setPath(request.getContextPath());
        response.addCookie(lastVisitCookie);

        // Передаем данные в Thymeleaf
        context.setVariable("visitCount", visitCount);
        context.setVariable("lastVisit", lastVisitTime.replace("_", " "));
    }

    /**
     * Определяет имя шаблона на основе пути запроса.
     */
    private String resolveTemplateName(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/index")) {
            return "subscribers/list";
        }
        // Убираем начальный слэш, чтобы получить путь вида "subscribers/list"
        return pathInfo.substring(1);
    }

    /**
     * Отображает указанный Thymeleaf шаблон.
     */
    private void renderTemplate(String templateName, WebContext context, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        templateEngine.process(templateName, context, response.getWriter());
    }

    /**
     * Централизованно обрабатывает ошибки и отображает страницу error.html.
     */
    private void handleError(Exception e, WebContext context, HttpServletResponse response) throws IOException {
        context.setVariable("errorTitle", "Произошла ошибка");
        context.setVariable("errorMessage", e.getMessage());
        context.setVariable("errorType", e.getClass().getSimpleName());
        
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        renderTemplate("error", context, response);
    }
}
