package controller;

import entity.Product;
import dao.ProductDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {

    // JDBC 연결 정보 (데이터베이스 연결)
    private static final String URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 파라미터 가져오기
        String prdName = request.getParameter("prdName");
        String prdCompany = request.getParameter("prdCompany");
        int prdPrice = Integer.parseInt(request.getParameter("prdPrice"));
        char prdAdult = request.getParameter("prdAdult").charAt(0);
        String prdExp = request.getParameter("prdExp");
        int prdStock = Integer.parseInt(request.getParameter("prdStock"));
        
        // 유통기한 처리 (빈 값일 경우 null 처리)
        java.sql.Date expDate = null;
        if (prdExp != null && !prdExp.isEmpty()) {
            expDate = java.sql.Date.valueOf(prdExp);
        }

        // Product 객체 생성
        Product product = new Product();
        product.setPrdName(prdName);
        product.setPrdCompany(prdCompany);
        product.setPrdPrice(prdPrice);
        product.setPrdAdult(prdAdult);
        product.setPrdStock(prdStock);
        product.setPrdExp(expDate);

        // 데이터베이스 연결 및 제품 등록
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            ProductDAOImpl productDAO = new ProductDAOImpl(conn);
            productDAO.insertProduct(product);
            
            // 등록 후, 목록 페이지로 리다이렉트 (등록 완료 후 리다이렉트)
            response.sendRedirect("productList.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("제품 등록 중 오류가 발생했습니다.");
        }
    }
}
