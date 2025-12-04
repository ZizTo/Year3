package com.zizto.controller;

import com.zizto.model.Role;
import com.zizto.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SecurityFilter implements Filter {

    // Список команд, доступных БЕЗ авторизации (для гостей)
    private static final List<String> PUBLIC_ACTIONS = Arrays.asList(
            "home", "loginPage", "login", "register", "saveUser"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String action = req.getParameter("action");
        if (action == null) action = "home";

        // 1. Если команда публичная - пропускаем без проверок
        if (PUBLIC_ACTIONS.contains(action)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Проверяем, вошел ли пользователь
        HttpSession session = req.getSession(false); // false = не создавать новую, если нет
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            // Если не вошел - отправляем на страницу входа с сообщением
            // (или на home, как просит задание, с предложением войти)
            req.setAttribute("message", "Please login to access this feature.");
            req.getRequestDispatcher("/?action=loginPage").forward(req, resp);
            return;
        }

        // 3. Проверка прав (Role-based access control)
        // Пример: Блокировка доступна только админу
        if ("blockSubscriber".equals(action) && user.getRole() != Role.ADMIN) {
             resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admins only.");
             return;
        }

        // Если все проверки пройдены - пропускаем запрос дальше к FrontController
        chain.doFilter(request, response);
    }
}
