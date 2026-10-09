package com.cartnova.servlet.buyer;

import com.cartnova.dao.CartDAO;
import com.cartnova.dao.WishlistDAO;
import com.cartnova.model.User;
import com.cartnova.model.WishlistItem;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {

    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<WishlistItem> wishlistItems = wishlistDAO.findByUserId(user.getUserId());
        req.setAttribute("wishlistItems", wishlistItems);
        req.getRequestDispatcher("/WEB-INF/views/buyer/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        try {
            int productId = Integer.parseInt(req.getParameter("productId"));

            if ("toggle".equalsIgnoreCase(action) || "add".equalsIgnoreCase(action)) {
                if (wishlistDAO.isInWishlist(user.getUserId(), productId)) {
                    wishlistDAO.remove(user.getUserId(), productId);
                } else {
                    wishlistDAO.add(user.getUserId(), productId);
                }
            } else if ("remove".equalsIgnoreCase(action)) {
                wishlistDAO.remove(user.getUserId(), productId);
            } else if ("moveToCart".equalsIgnoreCase(action)) {
                cartDAO.addItem(user.getUserId(), productId, 1);
                wishlistDAO.remove(user.getUserId(), productId);
            }
        } catch (NumberFormatException e) {
            System.err.println("WishlistServlet error: " + e.getMessage());
        }

        String redirect = req.getParameter("redirect");
        if (redirect != null && redirect.startsWith(req.getContextPath() + "/")
                && !redirect.startsWith("//") && !redirect.contains("\\")) {
            resp.sendRedirect(redirect);
        } else {
            resp.sendRedirect(req.getContextPath() + "/wishlist");
        }
    }

    private User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
