package com.zizto.listener;

import com.zizto.util.JpaUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * ServletContextListener для управления жизненным циклом JPA EntityManagerFactory.
 * Гарантирует, что EntityManagerFactory создается при старте приложения и
 * корректно закрывается при его остановке.
 */
@WebListener
public class JpaListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Вызывается при старте веб-приложения
        sce.getServletContext().log("Инициализация JPA...");
        JpaUtil.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Вызывается при остановке веб-приложения
        sce.getServletContext().log("Остановка JPA...");
        JpaUtil.shutdown();
    }
}
