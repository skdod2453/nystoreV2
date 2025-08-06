package controller;

import dao.EmployeeDAO;
import entity.Employee;
import dao.EmployeeDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

import static util.ConnectDB.getConnection;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public LoginServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String empId = request.getParameter("empId");
		String empPw = request.getParameter("empPw");

		try (Connection conn = getConnection()) {
			EmployeeDAO employeeDAO = new EmployeeDAOImpl(conn);
			Employee employee = employeeDAO.findByIdAndPassword(empId, empPw);

			if (employee != null) {
				// 로그인 성공
				HttpSession session = request.getSession();
				session.setAttribute("employee", employee);

				response.sendRedirect("main.jsp"); // 메인 페이지로 이동
			} else {
				// 로그인 실패
				request.setAttribute("error", "❗ 아이디 또는 비밀번호가 틀렸습니다.");
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "❗ DB 연결 오류");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}
}
