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

@WebServlet("/products")
public class ProductBrowseServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("q");
        String categoryIdStr = req.getParameter("category");
        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");
        String sort = req.getParameter("sort");

        Integer categoryId = null;
        Double minPrice = null;
        Double maxPrice = null;

        try {
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Integer.parseInt(categoryIdStr);
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = Double.parseDouble(minPriceStr);
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceStr);
            }
        } catch (NumberFormatException ignored) {}

        List<Product> products = productDAO.searchProducts(query, categoryId, minPrice, maxPrice, sort);
        List<Category> categories = categoryDAO.findAllActive();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("query", query);
        req.setAttribute("selectedCategory", categoryId);
        req.setAttribute("minPrice", minPriceStr);
        req.setAttribute("maxPrice", maxPriceStr);
        req.setAttribute("sort", sort);

        req.getRequestDispatcher("/WEB-INF/views/buyer/products.jsp").forward(req, resp);
    }
}
