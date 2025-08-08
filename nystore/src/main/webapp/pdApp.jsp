<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>제품 입력(등록)</title>
</head>
<body>
<h2>어떤 제품을 등록할까요?</h2>
<form method="post" action="ProductServlet">
    제품명 : <input type="text" name="prdName" required><br>
    제조회사 : <input type="text" name="prdCompany" required><br>
    가격 : <input type="number" name="prdPrice" required><br>
    19금 물품 여부 : 
    <select name="prdAdult">
        <option value="Y">Yes</option>
        <option value="N">No</option>
    </select><br>
    유통기한 : <input type="date" name="prdExp"><br>
    재고 : <input type="number" name="prdStock" required><br>
    <input type="submit" value="등록">
</form>
</body>
</html>