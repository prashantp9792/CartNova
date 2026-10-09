package com.cartnova.servlet.seller;

import com.cartnova.dao.OrderDAO;
import com.cartnova.dao.ProductDAO;
import com.cartnova.model.Order;
import com.cartnova.model.Product;
import com.cartnova.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/seller/reports")
public class SellerReportServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getLoggedInSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        BigDecimal revenue = orderDAO.getSellerRevenue(seller.getUserId());
        int totalOrders = orderDAO.countOrdersBySeller(seller.getUserId());
        List<Product> products = productDAO.findBySeller(seller.getUserId());
        List<Order> orders = orderDAO.findBySellerId(seller.getUserId());

        req.setAttribute("revenue", revenue);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("products", products);
        req.setAttribute("orders", orders);

        req.getRequestDispatcher("/WEB-INF/views/seller/reports.jsp").forward(req, resp);
    }

    private User getLoggedInSeller(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
