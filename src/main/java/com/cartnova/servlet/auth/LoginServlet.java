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
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            redirectByUserRole(user, req, resp);
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (!ValidationUtil.isValidEmail(email) || !ValidationUtil.isNotEmpty(password)) {
            req.setAttribute("error", "Please provide a valid email and password.");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isActive()) {
                req.setAttribute("error", "Your account has been deactivated. Please contact support.");
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
                return;
            }

            if (PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                HttpSession session = req.getSession(false);
                String redirectUrl = session == null ? null : (String) session.getAttribute("redirectUrl");
                if (session != null) {
                    session.invalidate();
                }
                session = req.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("role", user.getRole().name());

                if (redirectUrl != null && redirectUrl.startsWith(req.getContextPath() + "/")
                        && !redirectUrl.startsWith("//") && !redirectUrl.contains("\\")) {
                    resp.sendRedirect(redirectUrl);
                } else {
                    redirectByUserRole(user, req, resp);
                }
                return;
            }
        }

        req.setAttribute("error", "Invalid email or password.");
        req.setAttribute("email", email);
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    private void redirectByUserRole(User user, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (user.getRole()) {
            case ADMIN:
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                break;
            case SELLER:
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
                break;
            case BUYER:
            default:
                resp.sendRedirect(req.getContextPath() + "/home");
                break;
        }
    }
}
