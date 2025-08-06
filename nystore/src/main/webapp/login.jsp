<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>로그인</title></head>
<body>
<h2>직원 로그인</h2>
<form method="post" action="LoginServlet">
    아이디: <input type="text" name="empId"><br>
    비밀번호: <input type="password" name="empPw"><br>
    <input type="submit" value="로그인">
</form>

<% if (request.getAttribute("error") != null) { %>
    <p style="color:red;"><%= request.getAttribute("error") %></p>
<% } %>
</body>
</html>