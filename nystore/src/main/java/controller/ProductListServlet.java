package controller;

import entity.Product;
import dao.ProductDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import static util.ConnectDB.getConnection;

@WebServlet("/ProductListServlet")
public class ProductListServlet extends HttpServlet {

    // GET 요청 처리
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("doGet 호출됨");

        // DB 연결 및 제품 목록 조회
        try (Connection conn = getConnection()) {
            ProductDAOImpl productDAO = new ProductDAOImpl(conn);
            List<Product> products = productDAO.getAllProducts();  // 제품 목록 조회

            HttpSession session = request.getSession();
            session.setAttribute("products", products);  

            request.getRequestDispatcher("pdCheck.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("제품 목록 조회 중 오류가 발생했습니다.");
        }
    }
}