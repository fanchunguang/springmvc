package com.lagou.edu.demo.controller;

import com.lagou.edu.demo.service.IDemoService;
import com.lagou.edu.mvcframework.annotations.LagouAutowired;
import com.lagou.edu.mvcframework.annotations.LagouController;
import com.lagou.edu.mvcframework.annotations.LagouRequestMapping;
import com.lagou.edu.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@LagouController
@LagouRequestMapping("/demo")
public class DemoController {
    @LagouAutowired
    private IDemoService demoService;

    /**
     * URL: /demo/query?name=lisi
     * @param request
     * @param response
     ·* @param name
     * @return
     */
    @Security({"lisi"})
    @LagouRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response, String name) {
        return demoService.get(name);
    }

    @Security({"zhangsan,wangwu"})
    @LagouRequestMapping("/handler01")
    public String handler01(HttpServletRequest request, HttpServletResponse response,String username){
        return demoService.get(username);
    }

    @Security({"lisi"})
    @LagouRequestMapping("/handler02")
    public String handler02(HttpServletRequest request, HttpServletResponse response,String username){
        return demoService.get(username);
    }
}
