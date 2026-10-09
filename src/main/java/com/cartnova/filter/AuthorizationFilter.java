package com.cartnova.filter;

import com.cartnova.model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Role-Based Authorization Filter enforcing Admin and Seller route protection.
 * Prevents non-admin or non-seller users from accessing privileged endpoints.
 */
@WebFilter(urlPatterns = {"/admin/*", "/seller/*"})
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String path = req.getServletPath();

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (path.startsWith("/admin") && user.getRole() != User.Role.ADMIN) {
            req.setAttribute("errorMessage", "Access Denied: Admin privileges required.");
            req.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(req, resp);
            return;
        }

        if (path.startsWith("/seller") && user.getRole() != User.Role.SELLER && user.getRole() != User.Role.ADMIN) {
            req.setAttribute("errorMessage", "Access Denied: Seller account required.");
            req.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(req, resp);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
