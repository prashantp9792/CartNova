package com.cartnova.servlet.buyer;

import com.cartnova.dao.CartDAO;
import com.cartnova.dao.OrderDAO;
import com.cartnova.model.CartItem;
import com.cartnova.model.Order;
import com.cartnova.model.Payment;
import com.cartnova.model.User;
import com.cartnova.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<CartItem> cartItems = cartDAO.findByUserId(user.getUserId());
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("totalAmount", totalAmount);
        req.setAttribute("user", user);

        req.getRequestDispatcher("/WEB-INF/views/buyer/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<CartItem> cartItems = cartDAO.findByUserId(user.getUserId());
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        String addressLine1 = req.getParameter("addressLine1");
        String addressLine2 = req.getParameter("addressLine2");
        String city = req.getParameter("city");
        String state = req.getParameter("state");
        String zipCode = req.getParameter("zipCode");
        String paymentMethodStr = req.getParameter("paymentMethod");

        if (!ValidationUtil.isNotEmpty(addressLine1) || !ValidationUtil.isNotEmpty(city) ||
            !ValidationUtil.isNotEmpty(state) || !ValidationUtil.isNotEmpty(zipCode)) {
            req.setAttribute("error", "Please complete all required shipping address fields.");
            doGet(req, resp);
            return;
        }

        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(ValidationUtil.sanitize(addressLine1));
        if (ValidationUtil.isNotEmpty(addressLine2)) {
            fullAddress.append(", ").append(ValidationUtil.sanitize(addressLine2));
        }
        fullAddress.append(", ").append(ValidationUtil.sanitize(city))
                   .append(", ").append(ValidationUtil.sanitize(state))
                   .append(" - ").append(ValidationUtil.sanitize(zipCode));

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUserId(user.getUserId());
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.Status.CONFIRMED);
        order.setShippingAddress(fullAddress.toString());

        Payment.Method paymentMethod = Payment.Method.COD;
        try {
            if (paymentMethodStr != null) {
                paymentMethod = Payment.Method.valueOf(paymentMethodStr.toUpperCase());
            }
        } catch (IllegalArgumentException ignored) {}

        try {
            boolean success = orderDAO.placeOrder(order, cartItems, paymentMethod);
            if (success) {
                resp.sendRedirect(req.getContextPath() + "/orders/detail?id=" + order.getOrderId() + "&success=true");
                return;
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Order failed: " + e.getMessage());
            doGet(req, resp);
            return;
        }

        req.setAttribute("error", "Could not process order. Please check item availability.");
        doGet(req, resp);
    }

    private User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
