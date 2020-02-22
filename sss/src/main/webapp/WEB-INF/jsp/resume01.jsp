<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/2/12
  Time: 18:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getServerName() + ":" + request.getServerPort() + path + "/";
    String baseUrlPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<html>
<head>
    <script type="text/javascript" src="<%=baseUrlPath%>js/jquery.min.js"></script>
    <script type="text/javascript" src="<%=baseUrlPath%>js/resume.js"></script>
    <title>Title</title>

    <style type="text/css">
        .resumeTable{
            border-collapse:collapse;
            border-spacing:0;
            border-left:1px solid #888;
            border-top:1px solid #888;
            background:#efefef;
        }
        th,td{border-right:1px solid #888;border-bottom:1px solid #888;padding:5px 15px;}
        th{font-weight:bold;background:#ccc;}
    </style>

    <script type="text/javascript">

        initResumeData();

        $(function () {
            $("#addBtn").bind("click",function (i) {
                $("#resumeBody").append("" +
                    "<tr id='as"+i+"'>" +
                        "<td hidden><input id='id'/></td>" +
                        "<td><input id='name' type='text' style='width: auto'></td>" +
                        "<td><input id='address' type='text'></td>" +
                        "<td><input id='phone' type='text'></td>" +
                        '<td ><a id="edit" onclick="saveNew()">保存</a></td>'+
                        '<td ><!--<a href="javascript:void(0)" onclick="del()">删除</a>--></td>'+
                    "</tr>")
            })

        })

        function saveNew(){
            let name,address,phone
            $("#resumeBody tr:last").find("td").each(function (index) {
                debugger
                if(index==1){
                    name = $(this).find("input").val();
                }
                if(index==2){
                    address = $(this).find("input").val();
                }
                if(index==3){
                    phone = $(this).find("input").val();
                }
                console.info(name)
            })
            let resumeData={
                name:name,
                address:address,
                phone:phone
            }
            $.ajax({
                url:'/resume/save',
                type:'POST',
                dataType: 'json',
                data:resumeData,
                success:function(res){
                    debugger
                    if(res.success)
                        initResumeData();
                }
            })
        }
    </script>
</head>
<body>
    <div id="resumeDiv">
        <div id="btnDiv" style="margin-left: 400px">
            <a href="javascript:void(0)" id="addBtn">新增</a>
            <button  id="saveBtn" hidden>保存</button>
        </div>
        <div id="tableDiv">
            <table id="resumeTable" class="resumeTable" style="border: 1px solid">
                <tr>
                    <th hidden>序号</th>
                    <th>姓名</th>
                    <th>地址</th>
                    <th>电话</th>
                    <th>修改</th>
                    <th>删除</th>
                </tr>
                <tbody id="resumeBody">
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
