package com.cartnova.servlet.buyer;

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
import java.util.Optional;

@WebServlet(urlPatterns = {"/orders", "/orders/detail"})
public class OrderHistoryServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String servletPath = req.getServletPath();

        if ("/orders/detail".equals(servletPath)) {
            String idStr = req.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int orderId = Integer.parseInt(idStr);
                    Optional<Order> orderOpt = orderDAO.findById(orderId);

                    if (orderOpt.isPresent()) {
                        Order order = orderOpt.get();
                        // Security check: Buyers can view only their own orders (unless admin/seller)
                        if (order.getUserId() == user.getUserId() || user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.SELLER) {
                            req.setAttribute("order", order);
                            req.getRequestDispatcher("/WEB-INF/views/buyer/order-detail.jsp").forward(req, resp);
                            return;
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        List<Order> orders = orderDAO.findByUserId(user.getUserId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/WEB-INF/views/buyer/order-history.jsp").forward(req, resp);
    }

    private User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
