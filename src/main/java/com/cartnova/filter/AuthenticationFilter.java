package com.cartnova.filter;

import com.cartnova.model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication filter enforcing user session verification on protected routes.
 */
@WebFilter(urlPatterns = {
    "/cart/*", "/wishlist/*", "/checkout/*", "/orders/*", "/profile/*",
    "/seller/*", "/admin/*"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (loggedInUser == null) {
            String requestURI = req.getRequestURI();
            String queryString = req.getQueryString();
            String redirectUrl = requestURI + (queryString != null ? "?" + queryString : "");
            
            if (session == null) {
                session = req.getSession(true);
            }
            session.setAttribute("redirectUrl", redirectUrl);
            session.setAttribute("error", "Please log in to access this page.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
