<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/8/23
  Time: 10:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//	session.setAttribute("fid",request.getParameter("fid"));
//	session.setAttribute("uid",request.getParameter("uid"));
//	session.setAttribute("ip","10.7.10.78");
//	session.setAttribute("port","8888");
//	session.setAttribute("text","");
%>
<html>
<head>
  <meta charset="UTF-8">
  <title>FileUpload</title>
  <link href="static/bootstrap/css/bootstrap.css" rel="stylesheet"/>
  <link href="static/bootstrap/css/bootstrap-theme.css" rel="stylesheet"/>
  <link href="static/css/fileupload.css" rel="stylesheet"/>
  <script src="static/js/jquery-2.1.1.min.js"></script>
  <script src="static/bootstrap/js/bootstrap.min.js"></script>
  <script src="static/js/html5.websocket.fileupload-2.0.0.js"></script>
</head>
<body>
HHHHH
<div class="container">
  <div class="fileup"></div>
</div>
</body>
<script>
  $('.fileup').fileuploader({
    concurrentHash:3,
    concurrentUpload:3,
    debugMode:true,
    wsuri:'ws://localhost:8080/websocket/hh'
  });
</script>
</html>
