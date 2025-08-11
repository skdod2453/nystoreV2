<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.List, java.util.Random, entity.Product, entity.Employee" %>
<%!
    // JSP 선언부: Random 객체 한번만 생성
    private Random rand = new Random();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>랜덤 제품 입고</title>
</head>
<body>
<h2>랜덤 제품 입고</h2>

<%
    List<Product> products = (List<Product>) session.getAttribute("products");
    Product randomProduct = null;
    int randomQuantity = 0;

    if (products != null && !products.isEmpty()) {
        randomProduct = products.get(rand.nextInt(products.size()));
        randomQuantity = rand.nextInt(10) + 1; 
    }
    Employee loggedEmp = (Employee) session.getAttribute("employee");
    String empName = (loggedEmp != null) ? loggedEmp.getEmpName() : "";
%>

<% if (randomProduct != null) { %>
<form action="StockInServlet" method="post">
    <input type="hidden" name="prdId" value="<%= randomProduct.getPrdId() %>" />
    <input type="hidden" name="quantity" value="<%= randomQuantity %>" />
    <input type="hidden" name="updateEmp" value="<%= empName %>" />

    <p>제품명: <%= randomProduct.getPrdName() != null ? randomProduct.getPrdName() : "제품명 없음" %></p>
    <p>현재 재고: <%= randomProduct.getPrdStock() %></p>
    <p>입고 수량: <strong><%= randomQuantity %></strong> 개 (랜덤 자동 설정)</p>
    <p>입고 담당자: <strong>
        <% if (empName.isEmpty()) { %>
            로그인 필요 <a href="login.jsp">로그인 하러 가기</a>
        <% } else { %>
            <%= empName %>
        <% } %>
    </strong></p>

    <% if (!empName.isEmpty()) { %>
        <button type="submit">입고 저장</button>
    <% } else { %>
        <p style="color:red;">로그인 후 입고가 가능합니다.</p>
    <% } %>
</form>
<% } else { %>
    <p>제품 목록이 없습니다.</p>
<% } %>

<% if (request.getAttribute("error") != null) { %>
    <p style="color:red;"><%= request.getAttribute("error") %></p>
<% } %>

</body>
</html>
