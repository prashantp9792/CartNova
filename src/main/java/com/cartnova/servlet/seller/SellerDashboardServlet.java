package com.cartnova.servlet.seller;

import com.cartnova.dao.OrderDAO;
import com.cartnova.dao.ProductDAO;
import com.cartnova.model.Order;
import com.cartnova.model.Product;
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
import java.util.stream.Collectors;

@WebServlet("/seller/dashboard")
public class SellerDashboardServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getLoggedInSeller(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int totalProducts = productDAO.countBySeller(seller.getUserId());
        int totalOrders = orderDAO.countOrdersBySeller(seller.getUserId());
        BigDecimal totalRevenue = orderDAO.getSellerRevenue(seller.getUserId());

        List<Product> products = productDAO.findBySeller(seller.getUserId());
        List<Product> lowStockProducts = products.stream()
                .filter(p -> p.getStockQuantity() <= 5)
                .collect(Collectors.toList());

        List<Order> recentOrders = orderDAO.findBySellerId(seller.getUserId());
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5);
        }

        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("lowStockProducts", lowStockProducts);
        req.setAttribute("lowStockProductCount", lowStockProducts.size());
        req.setAttribute("recentOrders", recentOrders);

        req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
    }

    private User getLoggedInSeller(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
