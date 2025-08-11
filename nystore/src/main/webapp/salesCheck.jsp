<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.List, entity.Sales" %>

<%
    List<Sales> salesList = (List<Sales>) request.getAttribute("salesList");
    String selectedDate = (String) request.getAttribute("selectedDate");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>매출 내역</title>
</head>
<body>
<h2>매출 내역 - 날짜: <%= selectedDate %></h2>

<form method="get" action="SalesListServlet">
    날짜 선택: <input type="date" name="date" value="<%= selectedDate %>" />
    <button type="submit">조회</button>
</form>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
        <tr>
            <th>매출ID</th>
            <th>판매일자</th>
            <th>상품ID</th>
            <th>수량</th>
            <th>총금액</th>
            <th>결제수단</th>
            <th>카드번호</th>
            <th>현금</th>
            <th>거스름돈</th>
            <th>판매사원</th>
        </tr>
    </thead>
    <tbody>
        <%
            if (salesList != null && !salesList.isEmpty()) {
                for (Sales s : salesList) {
        %>
            <tr>
                <td><%= s.getSalesId() %></td>
                <td><%= s.getSaleDate() %></td>
                <td><%= s.getPrdId() %></td>
                <td><%= s.getQuantity() %></td>
                <td><%= s.getTotalPrice() %></td>
                <td><%= s.getPayment() %></td>
                <td><%= s.getCardNum() != null ? s.getCardNum() : "-" %></td>
                <td><%= s.getCash() > 0 ? s.getCash() : "-" %></td>
                <td><%= s.getCashChange() > 0 ? s.getCashChange() : "-" %></td>
                <td><%= s.getSoldEmp() %></td>
            </tr>
        <%
                }
            } else {
        %>
            <tr><td colspan="10">해당 날짜에 매출 내역이 없습니다.</td></tr>
        <%
            }
        %>
    </tbody>
</table>

</body>
</html>
