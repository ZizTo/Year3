package com.zizto.servlet;

import com.zizto.controller.IController;
import com.zizto.controller.actions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class ControllerMappings {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerMappings.class);
    private static final Map<String, IController> controllers = new HashMap<>();

    static {
        LOGGER.debug("Инициализация контролеов");
        
        controllers.put("GET:/", new ShowSubscriberListAction());
        controllers.put("GET:/index", new ShowSubscriberListAction());
        controllers.put("GET:/subscribers/list", new ShowSubscriberListAction());
        controllers.put("GET:/subscribers/details", new ShowSubscriberDetailsAction());
        
        controllers.put("POST:/subscribers/block", new BlockSubscriberAction());
        controllers.put("POST:/subscribers/payBill", new PayBillAction());
    }

    public static IController resolve(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            path = "/index";
        }
        
        String key = method + ":" + path;
        return controllers.get(key);
    }

    private ControllerMappings() {}
}
