package org.iplantc.de.server.auth;

import static org.iplantc.de.server.util.ServletUtils.loadResource;
import static org.iplantc.de.server.util.UrlUtils.convertRelativeUrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import org.stringtemplate.v4.ST;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A shared servlet for handling CAS authorization failure.
 *
 * @author Dennis Roberts, jstroot
 */
public class CasAccessDeniedServlet extends HttpServlet implements HttpRequestHandler {

    /**
     * The name of the template used to generate the HTML to return.
     */
    private static final String TEMPLATE_NAME = "access-denied-template.html";
    private final Logger LOG = LoggerFactory.getLogger(CasAccessDeniedServlet.class);

    /**
     * The URL used to log out of the application,
     */
    private String logoutUrl;

    /**
     * The text of the template used to generate the HTML to return.
     */
    private String templateText;

    public CasAccessDeniedServlet() {}

    public CasAccessDeniedServlet(final String logoutUrl){
        this.logoutUrl = logoutUrl;
        templateText = loadResource(TEMPLATE_NAME);
        LOG.info("Constructor args:\n\t" +
                      "logoutUrl = {}", logoutUrl);
    }

    @Override
    public void handleRequest(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
        if(request.getMethod().equalsIgnoreCase("GET")){
            doGet(request, response);
        }
    }

    /**
     * Generates the text to return.
     *
     * @param req the HTTP servlet request.
     * @return the generated HTML
     */
    private String generatePageText(HttpServletRequest req) {
        ST st = new ST(templateText, '$', '$');
        st.add("logout_url", convertRelativeUrl(req.getContextPath(), logoutUrl));
        return st.render();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        res.getWriter().print(generatePageText(req));
    }
}
