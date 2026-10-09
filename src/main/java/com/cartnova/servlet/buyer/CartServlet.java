package com.cartnova.servlet.buyer;

import com.cartnova.dao.CartDAO;
import com.cartnova.model.CartItem;
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

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<CartItem> cartItems = cartDAO.findByUserId(user.getUserId());
        BigDecimal grandTotal = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        req.setAttribute("cartItems", cartItems);
        req.setAttribute("grandTotal", grandTotal);
        req.getRequestDispatcher("/WEB-INF/views/buyer/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "add";

        try {
            switch (action) {
                case "add": {
                    int productId = Integer.parseInt(req.getParameter("productId"));
                    int quantity = 1;
                    if (req.getParameter("quantity") != null) {
                        quantity = Integer.parseInt(req.getParameter("quantity"));
                    }
                    cartDAO.addItem(user.getUserId(), productId, quantity);
                    break;
                }
                case "update": {
                    int productId = Integer.parseInt(req.getParameter("productId"));
                    int quantity = Integer.parseInt(req.getParameter("quantity"));
                    cartDAO.updateQuantity(user.getUserId(), productId, quantity);
                    break;
                }
                case "remove": {
                    int productId = Integer.parseInt(req.getParameter("productId"));
                    cartDAO.removeItem(user.getUserId(), productId);
                    break;
                }
                case "clear": {
                    cartDAO.clearCart(user.getUserId());
                    break;
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("CartServlet param parse error: " + e.getMessage());
        }

        // If AJAX request, return simple JSON count response
        if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
            int cartCount = cartDAO.getCartItemCount(user.getUserId());
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\": true, \"cartCount\": " + cartCount + "}");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
