package com.zizto.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;

/**
 * Общий интерфейс для всех контроллеров в приложении.
 * Обеспечивает единый способ обработки HTTP-запросов и подготовки данных для View (Thymeleaf).
 */
public interface IController {

    /**
     * Обрабатывает HTTP-запрос.
     *
     * @param request  объект HttpServletRequest
     * @param response объект HttpServletResponse
     * @param context  контекст Thymeleaf для передачи данных в шаблон
     * @throws Exception при возникновении ошибок
     */
    void process(HttpServletRequest request, HttpServletResponse response, WebContext context) throws Exception;
}
