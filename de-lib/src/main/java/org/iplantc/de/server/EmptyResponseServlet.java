package org.iplantc.de.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that does nothing but return an empty HTTP response.
 * 
 * @author Dennis Roberts
 */
public class EmptyResponseServlet extends HttpServlet {

    private static final long serialVersionUID = -5739800358156460256L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
