package controller;

import dao.ProductDAOImpl;
import dao.StockDAOImpl;
import entity.Stock;
import entity.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

import static util.ConnectDB.getConnection;

@WebServlet("/StockInServlet")
public class StockInServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int prdId = Integer.parseInt(request.getParameter("prdId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String updateEmp = request.getParameter("updateEmp");

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            ProductDAOImpl productDAO = new ProductDAOImpl(conn);
            StockDAOImpl stockDAO = new StockDAOImpl(conn);

            Product product = productDAO.getProductById(prdId);
            if (product == null) {
                request.setAttribute("error", "해당 제품이 존재하지 않습니다.");
                request.getRequestDispatcher("random.jsp").forward(request, response);
                return;
            }

            int newStock = product.getPrdStock() + quantity;
            productDAO.updateProduct(prdId, newStock);

            Stock stock = new Stock();
            stock.setPrdId(prdId);
            stock.setQuantity(quantity);
            stock.setUpdateEmp(updateEmp);
            stockDAO.insertStock(stock);

            conn.commit();

            // 입고 성공 후 제품 목록 갱신을 위해 기존 세션 초기화 후 다시 ProductListServlet으로 리다이렉트
            HttpSession session = request.getSession();
            session.removeAttribute("products"); // 기존 제품 목록 삭제

            response.sendRedirect("ProductListServlet"); // 제품 목록 새로 받아서 세션 세팅 후 jsp로 포워딩
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "입고 처리 중 오류가 발생했습니다.");
            request.getRequestDispatcher("random.jsp").forward(request, response);
        }
    }
}
