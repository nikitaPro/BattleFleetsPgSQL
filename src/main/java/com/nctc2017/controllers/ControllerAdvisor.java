package com.nctc2017.controllers;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ControllerAdvisor {
    private static final Logger LOG = Logger.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handle(Exception ex) {
        ModelAndView view = new ModelAndView("error");
        view.addObject("errorMes", "Arrr!!! Page not found");
        return view;
    }
    
    
    @ExceptionHandler(value = RuntimeException.class)
    public ModelAndView handleCustomException(RuntimeException ex) {
        LOG.error(ex);
        ModelAndView model = new ModelAndView("error");
        model.addObject("reason", ex.getMessage());
        
        return model;
    }
}