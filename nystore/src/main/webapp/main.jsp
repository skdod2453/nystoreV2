<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="entity.Employee" %>
<!DOCTYPE html>
<%
    Employee employee = (Employee) session.getAttribute("employee");
    if (employee == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
<head><title>메인페이지</title></head>
<body>
<h2><%= employee.getEmpName() %>님, 환영합니다!</h2>
<a href="pdAdd.jsp">1. 제품 입력</a><br/>
<a href="/nystore/ProductListServlet">2. 제품(재고) 확인</a><br/>
<a href="pdStock.jsp">3. 물품 입고</a><br/>
<a href="Count.jsp">4. 계산</a><br/>
<a href="salesCheck.jsp.">5. 매출 확인</a><br/>
<a href="logout.jsp">6. 로그아웃</a>
</body>
</html>