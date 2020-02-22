package com.lagou.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    private static final String LOGIN_ASSIGN = "admin";

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ModelAndView login(String username, String password,
                              ModelAndView mv, HttpSession session){
        if(username!=null && LOGIN_ASSIGN.equals(username)
                && password!=null && LOGIN_ASSIGN.equals(password)){
            session.setAttribute("login",true);
            mv.setViewName("redirect:resume01");
        }else{
            mv.setViewName("login");
        }
        return mv;
    }
}
