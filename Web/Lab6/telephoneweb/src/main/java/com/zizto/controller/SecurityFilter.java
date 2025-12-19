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

    private static final List<String> PUBLIC_ACTIONS = Arrays.asList(
            "home", "loginPage", "login", "register", "saveUser"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String action = req.getParameter("action");
        if (action == null) action = "home";

        if (PUBLIC_ACTIONS.contains(action)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            //req.setAttribute("message", "Please login to access this feature.");
            //req.getRequestDispatcher("/index?action=loginPage").forward(req, resp);
            return;
        }

        if ("blockSubscriber".equals(action) && user.getRole() != Role.ADMIN) {
             resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admins only.");
             return;
        }

        chain.doFilter(request, response);
    }
}
