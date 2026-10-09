package com.cartnova.servlet.seller;

import com.cartnova.dao.ProductDAO;
import com.cartnova.model.Product;
import com.cartnova.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/seller/inventory")
public class SellerInventoryServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getLoggedInSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Product> products = productDAO.findBySeller(seller.getUserId());
        req.setAttribute("products", products);
        req.getRequestDispatcher("/WEB-INF/views/seller/inventory.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getLoggedInSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int stock = Integer.parseInt(req.getParameter("stockQuantity"));

            // Verify ownership
            productDAO.findById(productId).ifPresent(p -> {
                if (p.getSellerId() == seller.getUserId()) {
                    productDAO.updateStock(productId, Math.max(0, stock));
                }
            });
        } catch (NumberFormatException ignored) {}

        resp.sendRedirect(req.getContextPath() + "/seller/inventory");
    }

    private User getLoggedInSeller(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
