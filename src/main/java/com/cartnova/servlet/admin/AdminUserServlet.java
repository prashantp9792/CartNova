package com.cartnova.servlet.admin;

import com.cartnova.dao.UserDAO;
import com.cartnova.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userDAO.findAll();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));

            if ("toggleStatus".equalsIgnoreCase(action)) {
                boolean active = Boolean.parseBoolean(req.getParameter("active"));
                userDAO.toggleActiveStatus(userId, active);
            } else if ("changeRole".equalsIgnoreCase(action)) {
                String roleStr = req.getParameter("role");
                if (roleStr != null) {
                    User.Role role = User.Role.valueOf(roleStr.toUpperCase());
                    userDAO.updateUserRole(userId, role);
                }
            }
        } catch (Exception e) {
            System.err.println("AdminUserServlet error: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}
