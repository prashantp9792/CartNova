package com.cartnova.servlet.seller;

import com.cartnova.dao.OrderDAO;
import com.cartnova.model.Order;
import com.cartnova.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/seller/orders")
public class SellerOrderServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getLoggedInSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Order> orders = orderDAO.findBySellerId(seller.getUserId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/WEB-INF/views/seller/orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getLoggedInSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            String statusStr = req.getParameter("status");

            if (statusStr != null) {
                Order.Status status = Order.Status.valueOf(statusStr.toUpperCase());
                orderDAO.updateStatusForSeller(orderId, seller.getUserId(), status);
            }
        } catch (IllegalArgumentException ignored) {}

        resp.sendRedirect(req.getContextPath() + "/seller/orders");
    }

    private User getLoggedInSeller(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
