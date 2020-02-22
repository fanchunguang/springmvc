package com.lagou.edu.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 */
public class LogoinInterceptor implements HandlerInterceptor {

    private static final String LOGIN_SIGN = "login";
    private static final String[] IGNORE_URI = {"/login"};
    /**
     * 对登录进行验证
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("request = " + request + ", response = " + response + ", handler = " + handler);
        Object attribute = request.getSession().getAttribute(LOGIN_SIGN);

        boolean flag = false;
        //获取请求的路径进行判断
        String servletPath = request.getServletPath();
        //判断请求是否需要拦截
        for (String s : IGNORE_URI) {
            if (servletPath.contains(s)) {
                flag = true;
                break;
            }
        }
        //拦截请求
        if (!flag) {
            //2.判断用户是否已经登录
            if(attribute == null) {
                //如果用户没有登录，跳转到登录页面
                response.sendRedirect("login");
            } else {
                //如果用户已经登录，则验证通过，放行
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
