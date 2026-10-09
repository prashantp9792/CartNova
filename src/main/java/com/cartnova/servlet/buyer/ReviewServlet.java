package com.cartnova.servlet.buyer;

import com.cartnova.dao.ReviewDAO;
import com.cartnova.model.Review;
import com.cartnova.model.User;
import com.cartnova.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("comment");

            if (rating >= 1 && rating <= 5) {
                Review review = new Review();
                review.setUserId(user.getUserId());
                review.setProductId(productId);
                review.setRating(rating);
                review.setComment(ValidationUtil.sanitize(comment));

                reviewDAO.addReview(review);
            }

            resp.sendRedirect(req.getContextPath() + "/product?id=" + productId + "#reviews");
            return;
        } catch (NumberFormatException e) {
            System.err.println("ReviewServlet error: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/products");
    }
}
