package com.cartnova.servlet.admin;

import com.cartnova.dao.CategoryDAO;
import com.cartnova.dao.OrderDAO;
import com.cartnova.dao.ProductDAO;
import com.cartnova.dao.UserDAO;
import com.cartnova.model.DashboardStats;
import com.cartnova.model.Order;
import com.cartnova.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DashboardStats stats = new DashboardStats();
        stats.setTotalUsers(userDAO.countAll());
        stats.setTotalSellers(userDAO.countByRole(User.Role.SELLER));
        stats.setTotalBuyers(userDAO.countByRole(User.Role.BUYER));
        stats.setTotalProducts(productDAO.countAll());
        stats.setTotalCategories(categoryDAO.findAll().size());
        stats.setTotalOrders(orderDAO.countOrders());
        stats.setTotalRevenue(orderDAO.getTotalRevenue());

        List<Order> recentOrders = orderDAO.findAll();
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5);
        }

        List<User> recentUsers = userDAO.findAll();
        if (recentUsers.size() > 5) {
            recentUsers = recentUsers.subList(0, 5);
        }

        req.setAttribute("stats", stats);
        req.setAttribute("recentOrders", recentOrders);
        req.setAttribute("recentUsers", recentUsers);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
