package com.zizto.controller.actions;

import com.zizto.controller.IController;
import com.zizto.controller.logic.SubscriberController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;

public class BlockSubscriberAction implements IController {
    private final SubscriberController logicController = new SubscriberController();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response, WebContext context) throws Exception {
        logicController.blockSubscriber(request, response);
    }
}
