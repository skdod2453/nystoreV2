<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.Product" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>제품 목록</title>
</head>
<body>
<h2>NyStore🏪 제품 목록</h2>

<%-- 제품 목록 가져오기 --%>
<%
	if (session != null) {
    	List<Product> products = (List<Product>) session.getAttribute("products");

    if (products != null && !products.isEmpty()) {
%>
    <table border="1">
        <thead>
            <tr>
                <th>상품명</th>
                <th>제조사</th>
                <th>가격</th>
                <th>연령 제한</th>
                <th>재고</th>
                <th>유통기한</th>
            </tr>
        </thead>
        <tbody>
            <%-- 제품 목록 출력 --%>
            <%
                for (Product product : products) {
            %>
            <tr>
                <td><%= product.getPrdName() %></td>
                <td><%= product.getPrdCompany() %></td>
                <td><%= product.getPrdPrice() %> 원</td>
                <td><%= product.getPrdAdult() == 'Y' ? "성인" : "일반" %></td>
                <td><%= product.getPrdStock() %> 개</td>
                <td><%= product.getPrdExp() != null ? product.getPrdExp().toString() : "없음" %></td>
            </tr>
            <%
                }
            %>
        </tbody>
    </table>
<%
        } else {
%>
    <p>등록된 제품이 없습니다.</p>
<%
        }
    } else {
%>
    <p>세션이 만료되었습니다. 다시 로그인해주세요.</p>
<%
    }
%>

</body>
</html>
