package com.cartnova.servlet.auth;

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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String roleStr = req.getParameter("role");

        if (!ValidationUtil.isNotEmpty(firstName) || !ValidationUtil.isNotEmpty(lastName)) {
            setErrorAndForward("First name and Last name are required.", req, resp);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            setErrorAndForward("Please enter a valid email address.", req, resp);
            return;
        }

        if (!ValidationUtil.isNotEmpty(password) || password.length() < 6) {
            setErrorAndForward("Password must be at least 6 characters long.", req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            setErrorAndForward("Passwords do not match.", req, resp);
            return;
        }

        if (userDAO.findByEmail(email.trim()).isPresent()) {
            setErrorAndForward("An account with this email already exists.", req, resp);
            return;
        }

        User.Role role = User.Role.BUYER;
        if ("SELLER".equalsIgnoreCase(roleStr)) {
            role = User.Role.SELLER;
        }

        User newUser = new User();
        newUser.setFirstName(ValidationUtil.sanitize(firstName));
        newUser.setLastName(ValidationUtil.sanitize(lastName));
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPhone(ValidationUtil.sanitize(phone));
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        newUser.setRole(role);
        newUser.setActive(true);

        if (userDAO.save(newUser)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", newUser);
            session.setAttribute("role", newUser.getRole().name());
            session.setAttribute("success", "Welcome to CartNova! Your account has been created.");

            if (role == User.Role.SELLER) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
            }
        } else {
            setErrorAndForward("Failed to create account due to a system error. Please try again.", req, resp);
        }
    }

    private void setErrorAndForward(String message, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("firstName", req.getParameter("firstName"));
        req.setAttribute("lastName", req.getParameter("lastName"));
        req.setAttribute("email", req.getParameter("email"));
        req.setAttribute("phone", req.getParameter("phone"));
        req.setAttribute("role", req.getParameter("role"));
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }
}
