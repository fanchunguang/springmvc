package com.lagou.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FormController {

    private static final String UREDIRECT="resume01";

    @RequestMapping(value="/{formName}")
    public String loginForm(@PathVariable String formName){
        return formName;
    }
}
