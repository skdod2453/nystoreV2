package controller;

import dao.ProductDAO;
import dao.ProductDAOImpl;
import dao.SalesDAO;
import dao.SalesDAOImpl;
import entity.Employee;
import entity.Product;
import entity.Sales;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static util.ConnectDB.getConnection;

@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            List<Product> products = productDAO.getAllProducts();

            request.setAttribute("products", products);
            request.getRequestDispatcher("/cart.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Employee currentEmployee = (Employee) session.getAttribute("employee");

        if (currentEmployee == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try (Connection conn = getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            SalesDAO salesDAO = new SalesDAOImpl(conn);

            List<Product> products = productDAO.getAllProducts();

            List<Sales> salesToInsert = new ArrayList<>();
            int totalAmount = 0;

            for (Product p : products) {
                String qtyStr = request.getParameter("quantity_" + p.getPrdId());
                if (qtyStr == null) continue;

                int qty = 0;
                try {
                    qty = Integer.parseInt(qtyStr);
                } catch (NumberFormatException e) {
                    // 무시
                }

                if (qty <= 0) continue;

                // 1) 재고 부족 체크
                if (qty > p.getPrdStock()) {
                    request.setAttribute("error", p.getPrdName() + " 재고 부족");
                    doGet(request, response);
                    return;
                }

                // 2) 유통기한 체크 (유통기한이 있고, 오늘 이전이면 구매 불가)
                Date expDate = (Date) p.getPrdExp();
                if (expDate != null && expDate.before(Date.valueOf(LocalDate.now()))) {
                    request.setAttribute("error", p.getPrdName() + " 유통기한이 지난 상품은 구매할 수 없습니다.");
                    doGet(request, response);
                    return;
                }

                // 3) 19금 상품 성인인증 체크
                if (p.getPrdAdult() == 'Y') {
                    String rrn = request.getParameter("rrn");
                    if (rrn == null || !isValidRrn(rrn)) {
                        request.setAttribute("error", p.getPrdName() + " 구매는 유효한 주민등록번호가 필요합니다.");
                        doGet(request, response);
                        return;
                    }
                    if (!isAdult(rrn)) {
                        request.setAttribute("error", "🔞 미성년자는 19금 상품 구매 불가합니다.");
                        doGet(request, response);
                        return;
                    }
                }

                int itemTotal = p.getPrdPrice() * qty;
                totalAmount += itemTotal;

                Sales sale = new Sales();
                sale.setPrdId(p.getPrdId());
                sale.setQuantity(qty);
                sale.setTotalPrice(itemTotal);
                sale.setSoldEmp(currentEmployee.getEmpId());
                salesToInsert.add(sale);
            }

            if (salesToInsert.isEmpty()) {
                request.setAttribute("error", "구매할 제품을 선택하세요.");
                doGet(request, response);
                return;
            }

            String payment = request.getParameter("payment");
            if (payment == null || !(payment.equals("CARD") || payment.equals("CASH"))) {
                request.setAttribute("error", "결제 수단을 선택하세요.");
                doGet(request, response);
                return;
            }

            String cardNum = null;
            int cash = 0;
            int change = 0;

            if (payment.equals("CARD")) {
                cardNum = request.getParameter("cardNum");
                if (cardNum == null || cardNum.trim().isEmpty()) {
                    request.setAttribute("error", "카드 번호를 입력하세요.");
                    doGet(request, response);
                    return;
                }
            } else { // 현금 결제
                String cashStr = request.getParameter("cash");
                try {
                    cash = Integer.parseInt(cashStr);
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "현금 금액을 정확히 입력하세요.");
                    doGet(request, response);
                    return;
                }
                if (cash < totalAmount) {
                    request.setAttribute("error", "현금이 부족합니다.");
                    doGet(request, response);
                    return;
                }
                change = cash - totalAmount;
            }

            // 매출 DB 저장 및 재고 업데이트
            for (Sales s : salesToInsert) {
                s.setPayment(payment);
                s.setCardNum(cardNum);
                s.setCash(payment.equals("CASH") ? cash : 0);
                s.setCashChange(payment.equals("CASH") ? change : 0);

                salesDAO.insertSale(s);

                // 재고 업데이트 (메서드 이름 맞춤)
                Product p = productDAO.getProductById(s.getPrdId());
                int newStock = p.getPrdStock() - s.getQuantity();
                productDAO.updateProduct(p.getPrdId(), newStock);
            }
            List<Product> updatedProducts = productDAO.getAllProducts();
            session.setAttribute("products", updatedProducts);
            request.setAttribute("message", "✅ 결제가 완료되었습니다. 총 금액: " + totalAmount + "원, 거스름돈: " + change + "원");
            doGet(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "서버 오류가 발생했습니다.");
            doGet(request, response);
        }
    }

    private boolean isValidRrn(String rrn) {
        return rrn.matches("\\d{6}-\\d");
    }

    private boolean isAdult(String rrn) {
        int birthYear = Integer.parseInt(rrn.substring(0, 2));
        int genderCode = Integer.parseInt(rrn.substring(7, 8));

        // 1,2,5,6 -> 1900년대 출생
        // 3,4,7,8 -> 2000년대 출생
        if (genderCode == 1 || genderCode == 2 || genderCode == 5 || genderCode == 6) {
            birthYear += 1900;
        } else if (genderCode == 3 || genderCode == 4 || genderCode == 7 || genderCode == 8) {
            birthYear += 2000;
        } else {
            return false; // 잘못된 성별코드
        }

        int currentYear = LocalDate.now().getYear();

        return (currentYear - birthYear) >= 19;
    }

}
