package com.cartnova.servlet.buyer;

import com.cartnova.dao.UserDAO;
import com.cartnova.model.User;
import com.cartnova.util.PasswordUtil;
import com.cartnova.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        userDAO.findById(user.getUserId()).ifPresent(u -> req.getSession().setAttribute("user", u));
        req.getRequestDispatcher("/WEB-INF/views/buyer/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        if ("changePassword".equalsIgnoreCase(action)) {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            if (!PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
                req.setAttribute("error", "Current password is incorrect.");
            } else if (!ValidationUtil.isNotEmpty(newPassword) || newPassword.length() < 6) {
                req.setAttribute("error", "New password must be at least 6 characters long.");
            } else if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("error", "New passwords do not match.");
            } else {
                if (userDAO.updatePassword(user.getUserId(), PasswordUtil.hashPassword(newPassword))) {
                    req.setAttribute("success", "Password updated successfully!");
                } else {
                    req.setAttribute("error", "Could not update your password. Please try again.");
                }
            }
        } else {
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phone = req.getParameter("phone");
            String addressLine1 = req.getParameter("addressLine1");
            String addressLine2 = req.getParameter("addressLine2");
            String city = req.getParameter("city");
            String state = req.getParameter("state");
            String zipCode = req.getParameter("zipCode");

            if (ValidationUtil.isNotEmpty(firstName) && ValidationUtil.isNotEmpty(lastName)) {
                user.setFirstName(ValidationUtil.sanitize(firstName));
                user.setLastName(ValidationUtil.sanitize(lastName));
                user.setPhone(ValidationUtil.sanitize(phone));
                user.setAddressLine1(ValidationUtil.sanitize(addressLine1));
                user.setAddressLine2(ValidationUtil.sanitize(addressLine2));
                user.setCity(ValidationUtil.sanitize(city));
                user.setState(ValidationUtil.sanitize(state));
                user.setZipCode(ValidationUtil.sanitize(zipCode));

                if (userDAO.updateProfile(user)) {
                    req.getSession().setAttribute("user", user);
                    req.setAttribute("success", "Profile updated successfully!");
                } else {
                    req.setAttribute("error", "Failed to update profile.");
                }
            } else {
                req.setAttribute("error", "First Name and Last Name are required.");
            }
        }

        doGet(req, resp);
    }

    private User getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
}
