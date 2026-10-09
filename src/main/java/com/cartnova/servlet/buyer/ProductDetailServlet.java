package com.cartnova.servlet.buyer;

import com.cartnova.dao.ProductDAO;
import com.cartnova.dao.ReviewDAO;
import com.cartnova.dao.WishlistDAO;
import com.cartnova.model.Product;
import com.cartnova.model.Review;
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

@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            int productId = Integer.parseInt(idStr);
            Optional<Product> pOpt = productDAO.findById(productId);

            if (pOpt.isPresent()) {
                Product product = pOpt.get();
                List<Review> reviews = reviewDAO.findByProductId(productId);
                List<Product> relatedProducts = productDAO.searchProducts(null, product.getCategoryId(), null, null, null);

                // Check wishlist status if user is logged in
                HttpSession session = req.getSession(false);
                boolean inWishlist = false;
                if (session != null && session.getAttribute("user") != null) {
                    User user = (User) session.getAttribute("user");
                    inWishlist = wishlistDAO.isInWishlist(user.getUserId(), productId);
                }

                req.setAttribute("product", product);
                req.setAttribute("reviews", reviews);
                req.setAttribute("relatedProducts", relatedProducts);
                req.setAttribute("inWishlist", inWishlist);

                req.getRequestDispatcher("/WEB-INF/views/buyer/product-detail.jsp").forward(req, resp);
                return;
            }
        } catch (NumberFormatException ignored) {}

        resp.sendRedirect(req.getContextPath() + "/products");
    }
}
