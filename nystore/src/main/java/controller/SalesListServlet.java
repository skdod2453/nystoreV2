package controller;

import dao.SalesDAO;
import dao.SalesDAOImpl;
import entity.Sales;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import static util.ConnectDB.getConnection;

@WebServlet("/SalesListServlet")
public class SalesListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String date = request.getParameter("date");  // yyyy-MM-dd 형식 기대
        if (date == null || date.isEmpty()) {
            date = java.time.LocalDate.now().toString();  // 오늘 날짜 기본값
        }

        try (Connection conn = getConnection()) {
            SalesDAO salesDAO = new SalesDAOImpl(conn);
            List<Sales> salesList = salesDAO.getSalesByDate(date);

            request.setAttribute("salesList", salesList);
            request.setAttribute("selectedDate", date);

            request.getRequestDispatcher("/salesCheck.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "서버 오류");
        }
    }
}
