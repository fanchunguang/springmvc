<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/2/12
  Time: 18:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page%>
<%
        String path = request.getContextPath();
        String basePath = request.getServerName() + ":" + request.getServerPort() + path + "/";
        String baseUrlPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>登录</title>
    <script type="text/javascript" src="<%=baseUrlPath%>js/jquery.min.js"></script>
    <script type="text/javascript" src="<%=baseUrlPath%>js/login.js"></script>
</head>
<body>
    <div id="login" class="login" style="text-align: center">
        <form class="loginForm" method="post" action="/login.do">
            <table>
                <tr>
                    <td><label>用户名：</label></td>
                    <td><input type="text" id="username" name="username"></td>
                </tr>
                <tr>
                    <td><label>密码：</label></td>
                    <td><input type="password" id="password" name="password"></td>
                </tr>
                <tr>
                    <td>
                        <input type="submit" value="登录">
                    </td>
                </tr>
            </table>
        </form>
    </div>
</body>
</html>
