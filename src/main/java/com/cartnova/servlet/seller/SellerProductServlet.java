package com.cartnova.servlet.seller;

import com.cartnova.dao.CategoryDAO;
import com.cartnova.dao.ProductDAO;
import com.cartnova.model.Category;
import com.cartnova.model.Product;
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
import java.util.List;
import java.util.Optional;

@WebServlet("/seller/products")
public class SellerProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getLoggedInSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        if ("add".equalsIgnoreCase(action) || "edit".equalsIgnoreCase(action)) {
            List<Category> categories = categoryDAO.findAllActive();
            req.setAttribute("categories", categories);

            if ("edit".equalsIgnoreCase(action)) {
                try {
                    int productId = Integer.parseInt(req.getParameter("id"));
                    Optional<Product> pOpt = productDAO.findById(productId);
                    if (pOpt.isPresent() && pOpt.get().getSellerId() == seller.getUserId()) {
                        req.setAttribute("product", pOpt.get());
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/seller/products");
                        return;
                    }
                } catch (NumberFormatException ignored) {}
            }
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
            return;
        }

        List<Product> products = productDAO.findBySeller(seller.getUserId());
        req.setAttribute("products", products);
        req.getRequestDispatcher("/WEB-INF/views/seller/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getLoggedInSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        if ("delete".equalsIgnoreCase(action)) {
            try {
                int productId = Integer.parseInt(req.getParameter("productId"));
                productDAO.delete(productId, seller.getUserId());
            } catch (NumberFormatException ignored) {}
            resp.sendRedirect(req.getContextPath() + "/seller/products");
            return;
        }

        String productIdStr = req.getParameter("productId");
        String name = req.getParameter("name");
        String categoryIdStr = req.getParameter("categoryId");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQuantity");
        String description = req.getParameter("description");
        String imageUrl = req.getParameter("imageUrl");
        String brand = req.getParameter("brand");
        boolean active = "on".equalsIgnoreCase(req.getParameter("active")) || "true".equalsIgnoreCase(req.getParameter("active"));

        if (!ValidationUtil.isNotEmpty(name) || !ValidationUtil.isNotEmpty(priceStr) || !ValidationUtil.isNotEmpty(stockStr)) {
            req.setAttribute("error", "Name, Price, and Stock Quantity are required.");
            req.setAttribute("categories", categoryDAO.findAllActive());
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
            return;
        }

        try {
            int categoryId = Integer.parseInt(categoryIdStr);
            BigDecimal price = new BigDecimal(priceStr);
            int stockQuantity = Integer.parseInt(stockStr);

            Product p = new Product();
            p.setSellerId(seller.getUserId());
            p.setCategoryId(categoryId);
            p.setName(ValidationUtil.sanitize(name));
            p.setDescription(ValidationUtil.sanitize(description));
            p.setPrice(price);
            p.setStockQuantity(stockQuantity);
            p.setImageUrl(ValidationUtil.isNotEmpty(imageUrl) ? imageUrl.trim() : "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80");
            p.setBrand(ValidationUtil.sanitize(brand));
            p.setActive(active);

            if (ValidationUtil.isNotEmpty(productIdStr)) {
                p.setProductId(Integer.parseInt(productIdStr));
                productDAO.update(p);
            } else {
                productDAO.save(p);
            }

            resp.sendRedirect(req.getContextPath() + "/seller/products");
            return;
        } catch (Exception e) {
            req.setAttribute("error", "Please enter a valid category, price, and stock quantity.");
            req.setAttribute("categories", categoryDAO.findAllActive());
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        }
    }

    private User getLoggedInSeller(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
