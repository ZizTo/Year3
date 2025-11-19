package com.zizto.servlet;

import com.zizto.controller.IController;
import com.zizto.controller.actions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Реестр для сопоставления URL-адресов с соответствующими контроллерами.
 * Заменяет сложную логику if/else или switch в сервлете.
 */
public class ControllerMappings {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerMappings.class);
    private static final Map<String, IController> controllers = new HashMap<>();

    static {
        LOGGER.debug("Инициализация маппингов контроллеров...");
        
        // GET запросы
        controllers.put("GET:/", new ShowSubscriberListAction());
        controllers.put("GET:/index", new ShowSubscriberListAction());
        controllers.put("GET:/subscribers/list", new ShowSubscriberListAction());
        controllers.put("GET:/subscribers/details", new ShowSubscriberDetailsAction());
        
        // POST запросы
        controllers.put("POST:/subscribers/block", new BlockSubscriberAction());
        controllers.put("POST:/subscribers/payBill", new PayBillAction());

        LOGGER.debug("Маппинги успешно инициализированы. Зарегистрировано {} контроллеров.", controllers.size());
    }

    /**
     * Находит контроллер для данного HTTP-запроса.
     * @param request HTTP-запрос
     * @return IController, соответствующий запросу, или null, если не найден.
     */
    public static IController resolve(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/index"; // Главная страница по умолчанию
        }
        
        String key = method + ":" + path;
        LOGGER.trace("Поиск контроллера для ключа: {}", key);
        return controllers.get(key);
    }

    // Приватный конструктор, чтобы предотвратить создание экземпляров
    private ControllerMappings() {}
}
