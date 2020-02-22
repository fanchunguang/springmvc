package com.lagou.edu.mvcframework.servlet;

import com.lagou.edu.mvcframework.annotations.*;
import com.lagou.edu.mvcframework.pojo.Handler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LgDispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    private List<String> classNames = new ArrayList<>(); // 缓存扫描到的类的全限定类名

    // ioc容器
    private Map<String,Object> ioc = new HashMap<>();
    //存储url和方法之前的关系
    private List<Handler> handlerMapping = new ArrayList<>();
    //存储权限url和用户之间的对应关系
    private Map<String,String[]> securityMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置文件 springmvc.properties
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        doLoadConfig(contextConfigLocation);

        //2.扫描类，扫描注解
        doScan(properties.getProperty("scanPackage"));

        //3.初始化bean对象（实现ioc容器，基于注解）
        doInstance();

        //4.实现依赖注入
        doAutowired();

        //5.构造一个handlerMapping处理器映射器，将配置好的url和method建立映射关系
        initHandlerMapping();

        //6.建立用户名，url权限对应关系
        doPrivilege();

        System.out.println("lagou mvc 初始化完成..... ");
        //等待请求进入，处理请求
    }

    private void doPrivilege() {
        if(ioc.isEmpty())
            return;
        for(Map.Entry<String,Object> entry: ioc.entrySet()){
            Class<?> aClass = entry.getValue().getClass();
            if(!aClass.isAnnotationPresent(LagouController.class)){
                continue;
            }
            String baseUrl = "";
            if(aClass.isAnnotationPresent(LagouRequestMapping.class)) {
                LagouRequestMapping annotation = aClass.getAnnotation(LagouRequestMapping.class);
                baseUrl = annotation.value(); // 等同于/demo
            }

            Method[] declaredMethod = aClass.getMethods();
            for (int i = 0; i < declaredMethod.length; i++) {
                Method method = declaredMethod[i];
                if(!method.isAnnotationPresent(Security.class))
                    continue;

                // 如果标识，就处理
                LagouRequestMapping annotation = method.getAnnotation(LagouRequestMapping.class);
                String methodUrl = annotation.value();  // /query
                String url = baseUrl + methodUrl;    // 计算出来的url /demo/query
                //处理权限标识
                String[] value = method.getAnnotation(Security.class).value();
                for (String s : value) {
                    if(!securityMap.containsKey(s)){
                        securityMap.put(s,new String[]{ url });
                    }else{
                        String[] tempStr = securityMap.get(s);
                        String[] newAdds = ArrayUtils.add(tempStr, url);
                        securityMap.put(s,newAdds);
                    }
                }
            }

        }
    }

    /**
     * 构造一个handlerMapping处理器映射器
     * 目的：将url和method建立关联
     */
    private void initHandlerMapping() {
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry: ioc.entrySet()) {
            // 获取ioc中当前遍历的对象的class类型
            Class<?> aClass = entry.getValue().getClass();

            if(!aClass.isAnnotationPresent(LagouController.class)) {continue;}

            String baseUrl = "";
            if(aClass.isAnnotationPresent(LagouRequestMapping.class)) {
                LagouRequestMapping annotation = aClass.getAnnotation(LagouRequestMapping.class);
                baseUrl = annotation.value(); // 等同于/demo
            }

            // 获取方法
            Method[] methods = aClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                //  方法没有标识LagouRequestMapping，就不处理
                if(!method.isAnnotationPresent(LagouRequestMapping.class)) {continue;}

                // 如果标识，就处理
                LagouRequestMapping annotation = method.getAnnotation(LagouRequestMapping.class);
                String methodUrl = annotation.value();  // /query
                String url = baseUrl + methodUrl;    // 计算出来的url /demo/query

                // 把method所有信息及url封装为一个Handler
                Handler handler = new Handler(entry.getValue(),method, Pattern.compile(url));

                // 计算方法的参数位置信息  // query(HttpServletRequest request, HttpServletResponse response,String name)
                Parameter[] parameters = method.getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];

                    if(parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class) {
                        // 如果是request和response对象，那么参数名称写HttpServletRequest和HttpServletResponse
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(),j);
                    }else{
                        handler.getParamIndexMapping().put(parameter.getName(),j);  // <name,2>
                    }

                }
                // 建立url和method之间的映射关系（map缓存起来）
                handlerMapping.add(handler);
            }
        }
    }

    //实现依赖注入
    private void doAutowired() {
        if(ioc.isEmpty())
            return;
        //遍历ioc中所有对象，查看对象中的字段，是否有@LagouAutowired注解，如果有需要维护依赖注入关系
        for(Map.Entry<String,Object> map:ioc.entrySet()){
            //获取所有字段
            Field[] fields = map.getValue().getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if(!field.isAnnotationPresent(LagouAutowired.class)){
                    continue;
                }
                //有该注解
                LagouAutowired annotation = field.getAnnotation(LagouAutowired.class);
                String beanName = annotation.value();
                if("".equals(beanName.trim())){
                    //没有配置具体的bean id,那就需要根据当前字段类型注入（接口注入）
                   beanName = field.getType().getName();
                }
                field.setAccessible(true);

                try {
                    field.set(map.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 基于chassNames缓存的类的全限定名，以及反射技术，完成对象创建和管理
     */
    private void doInstance() {
        if(classNames ==null || classNames.size()==0)
            return;

        try {
            for (int i = 0; i < classNames.size(); i++) {
                String className = classNames.get(i);
                //反射
                Class<?> aclass = Class.forName(className);
                if(aclass.isAnnotationPresent(LagouController.class)){
                    //controller 的id不过多处理，直接拿首字母小写作为id,保存到ioc容器中
                    String simpleName = aclass.getSimpleName();
                    ioc.put(lowerFirst(simpleName),aclass.newInstance());
                }else if(aclass.isAnnotationPresent(LagouService.class)){
                    LagouService annotation = aclass.getAnnotation(LagouService.class);
                    //获得注解的值
                    String beanName = annotation.value();
                    //如果指定了id,则存储id值为beanName得值
                    if(!"".equals(beanName.trim())){
                        ioc.put(beanName,aclass.newInstance());
                    }else{
                        String simpleName = aclass.getSimpleName();
                        ioc.put(lowerFirst(simpleName),aclass.newInstance());
                    }

                    //service层是有接口的 以接口名为id,存入到ioc容器中便于接口注入
                    Class<?>[] interfaces = aclass.getInterfaces();
                    for (int j = 0; j < interfaces.length; j++) {
                        Class<?> anInterface = interfaces[j];
                        //以接口的全限定类名作为id放入
                        ioc.put(anInterface.getName(),aclass.newInstance());
                    }
                }else{
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 首字母小写
     * @param str
     * @return
     */
    public String lowerFirst(String str){
        char[] chars = str.toCharArray();
        if('A' <chars[0] && chars[0] <'Z'){
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    /**
     * 扫描类
     * @param scanPackage
     */
    private void doScan(String scanPackage) {
        String scanPackagePath = Thread.currentThread().getContextClassLoader().
                getResource("").getPath() + scanPackage.replaceAll("\\.","/");
        File pack = new File(scanPackagePath);

        File[] files = pack.listFiles();
        for (File file:files) {
            if(file.isDirectory()){
                //递归
                doScan(scanPackage +"." +file.getName());
            }else if(file.getName().endsWith(".class")) {
                String className = scanPackage+"." + file.getName().replaceAll(".class","");
                classNames.add(className);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //处理请求：根据url，找到对应的Method方法，进行调用
        // 根据uri获取到能够处理当前请求的hanlder（从handlermapping中（list））
        Handler handler = getHanler(req);

        if(handler ==null){
            resp.getWriter().write("404 not found");
            return;
        }
        String tmpValue="";
        //参数绑定
        // 获取所有参数类型数组，这个数组的长度就是我们最后要传入的args数组的长度
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        //根据上述数组长度创建一个新的数组（参数数组，是要传入反射调用的）
        Object[] paraValues = new Object[parameterTypes.length];
        //向参数数组中塞值，保证参数的顺序和方法中形参保持一致
        Map<String,String[]> parameterMap = req.getParameterMap();
        // 遍历request中所有参数  （填充除了request，response之外的参数）
        for(Map.Entry<String,String[]> param: parameterMap.entrySet()) {
            // name=1&name=2   name [1,2]
            String value = StringUtils.join(param.getValue(), ",");  // 如同 1,2
            // 如果参数和方法中的参数匹配上了，填充数据
            if(!handler.getParamIndexMapping().containsKey(param.getKey())) {continue;}

            // 方法形参确实有该参数，找到它的索引位置，对应的把参数值放入paraValues
            Integer index = handler.getParamIndexMapping().get(param.getKey());//name在第 2 个位置
            tmpValue = value;
            paraValues[index] = value;  // 把前台传递过来的参数值填充到对应的位置去
        }

        int requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName()); // 0
        paraValues[requestIndex] = req;

        int responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName()); // 1
        paraValues[responseIndex] = resp;

        //权限判断
        String[] strings = securityMap.get(tmpValue);
        if(strings ==null||strings.length ==0){
            return;
        }
        boolean isSecurity = false;
        for (int i = 0; i < strings.length; i++) {
            String url = strings[i];
            System.out.println("url = " + url + ", pattern = " + handler.getPattern().toString());
            if(url.equals(handler.getPattern().toString())){
                isSecurity = true;
            }
        }
        if(!isSecurity){
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Content-type", "text/html;charset=UTF-8");
            resp.getWriter().write("无访问权限");
            return;
        }

        // 最终调用handler的method属性
        try {
            handler.getMethod().invoke(handler.getController(),paraValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler getHanler(HttpServletRequest request){
        if(handlerMapping.isEmpty()){return null;}

        String url = request.getRequestURI();
        for(Handler handler: handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){continue;}
            return handler;
        }
        return null;
    }
}
