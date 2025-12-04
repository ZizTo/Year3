package com.zizto.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;


public interface IController {

    void process(HttpServletRequest request, HttpServletResponse response, WebContext context) throws Exception;
}
