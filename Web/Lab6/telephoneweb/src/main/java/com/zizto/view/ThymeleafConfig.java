package com.zizto.view;

import jakarta.servlet.ServletContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

public class ThymeleafConfig {

    public static TemplateEngine getTemplateEngine(ServletContext servletContext) {
        
        // 1. Создаем "обертку" приложения (Специфика Thymeleaf 3.1)
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);

        // 2. Используем WebApplicationTemplateResolver вместо старого ServletContextTemplateResolver
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
        
        // Настройки остаются прежними
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }
}
