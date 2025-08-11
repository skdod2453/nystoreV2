<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.util.List, java.time.LocalDate, java.time.ZoneId, java.util.Date, entity.Product" %>

<%
    List<Product> products = (List<Product>) session.getAttribute("products");
    String error = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");
    LocalDate today = LocalDate.now();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>장바구니 및 결제</title>

    <script>
    function checkAdultAndRrn() {
        // 19금 상품 중 구매 수량이 0 초과인게 있는지 확인
        let hasAdultProduct = false;

        <% if (products != null) {
            for (Product p : products) {
                if (p.getPrdAdult() == 'Y') { %>
                    const qty_<%=p.getPrdId()%> = parseInt(document.querySelector('input[name="quantity_<%=p.getPrdId()%>"]').value) || 0;
                    if (qty_<%=p.getPrdId()%> > 0) {
                        hasAdultProduct = true;
                    }
        <%      }
            }
        } %>

        if (!hasAdultProduct) {
            // 19금 상품 없으면 바로 제출
            return true;
        }

        // 19금 상품 있을 때 주민등록번호 입력받기
        let rrn = prompt("19금 상품 구매 시 주민등록번호를 입력해주세요 (예: 123456-1)");
        if (rrn == null || rrn.trim() === "") {
            alert("주민등록번호를 입력해야 19금 상품 구매가 가능합니다.");
            return false;
        }

        // 형식 체크 (6자리 숫자-7자리 숫자)
        const rrnRegex = /^\d{6}-\d{1}$/;
	    if (!rrnRegex.test(rrn)) {
	        alert("유효한 주민등록번호 형식이 아닙니다. 예: 990101-1");
	        return false;
	    }

        // 폼에 숨겨진 필드에 주민등록번호 세팅
        let rrnInput = document.querySelector('input[name="rrn"]');
        if (!rrnInput) {
            rrnInput = document.createElement('input');
            rrnInput.type = 'hidden';
            rrnInput.name = 'rrn';
            document.forms[0].appendChild(rrnInput);
        }
        rrnInput.value = rrn;

        return true; // 폼 제출 허용
    }
    </script>
</head>
<body>

<h2>상품 목록</h2>

<% if (error != null) { %>
    <p style="color:red;"><%= error %></p>
<% } %>

<% if (message != null) { %>
    <p style="color:green;"><%= message %></p>
<% } %>

<form method="post" action="CheckoutServlet" onsubmit="return checkAdultAndRrn()">
    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr>
                <th>상품명</th>
                <th>가격</th>
                <th>재고</th>
                <th>유통기한</th>
                <th>19금</th>
                <th>구매 수량</th>
            </tr>
        </thead>
        <tbody>
        <%
        if (products != null && !products.isEmpty()) {
            for (Product p : products) {
                java.sql.Date expireDate = (java.sql.Date) p.getPrdExp();
                LocalDate expireLocal = null;
                if (expireDate != null) {
                    expireLocal = expireDate.toLocalDate();  
                }
                boolean isExpired = expireLocal != null && expireLocal.isBefore(today);

                if (p.getPrdStock() > 0 && !isExpired) {
        %>
            <tr>
                <td><%= p.getPrdName() %></td>
                <td><%= p.getPrdPrice() %> 원</td>
                <td><%= p.getPrdStock() %></td>
                <td><%= expireLocal != null ? expireLocal.toString() : "정보없음" %></td>
                <td><%= (p.getPrdAdult() == 'Y') ? "19금" : "-" %></td>
                <td>
                    <input type="number" 
                           name="quantity_<%= p.getPrdId() %>" 
                           min="0" 
                           max="<%= p.getPrdStock() %>" 
                           value="0" />
                </td>
            </tr>
        <%
                }
            }
        } else {
        %>
            <tr><td colspan="6">상품 목록이 없습니다.</td></tr>
        <%
        }
        %>
        </tbody>
    </table>

    <!-- 주민등록번호 숨은 필드 -->
    <input type="hidden" name="rrn" value="" />

    <p>
        결제 수단:
        <select name="payment" required>
            <option value="">선택</option>
            <option value="CARD">카드</option>
            <option value="CASH">현금</option>
        </select>
    </p>

    <p>
        카드 번호 (카드 결제 시 입력):
        <input type="text" name="cardNum" />
    </p>

    <p>
        현금 금액 (현금 결제 시 입력):
        <input type="number" name="cash" min="0" />
    </p>

    <button type="submit">결제하기</button>
</form>

</body>
</html>
