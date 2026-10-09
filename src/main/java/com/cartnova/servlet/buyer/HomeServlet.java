package com.cartnova.servlet.buyer;

import com.cartnova.dao.CategoryDAO;
import com.cartnova.dao.ProductDAO;
import com.cartnova.model.Category;
import com.cartnova.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Category> categories = categoryDAO.findAllActive();
        List<Product> featuredProducts = productDAO.searchProducts(null, null, null, null, "rating");

        if (featuredProducts.size() > 8) {
            featuredProducts = featuredProducts.subList(0, 8);
        }

        req.setAttribute("categories", categories);
        req.setAttribute("featuredProducts", featuredProducts);
        req.getRequestDispatcher("/WEB-INF/views/buyer/home.jsp").forward(req, resp);
    }
}
