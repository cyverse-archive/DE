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
 * A shared servlet for displaying a logout-successful page.
 *
 * @author Dennis Roberts, jstroot
 */
public class CasLoggedOutServlet extends HttpServlet implements HttpRequestHandler {

    /**
     * The name of the template used to generate the logout success page.
     */
    private static final String TEMPLATE_NAME = "logged-out-template.html";
    private final Logger LOG = LoggerFactory.getLogger(CasLoggedOutServlet.class);

    /**
     * The application name to display in the logout success page.
     */
    private String appName;

    /**
     * The login URL to use in the logout success page.
     */
    private String loginUrl;

    /**
     * The contents of the template used to generate the logout success page.
     */
    private String templateText;

    /**
     * The default constructor.
     */
    public CasLoggedOutServlet() {
    }

    public CasLoggedOutServlet(final String appName, final String loginUrl){
        this.appName = appName;
        this.loginUrl = loginUrl;
        this.templateText = loadResource(TEMPLATE_NAME);
        LOG.debug("Constructor args: \n\t" +
                      "appName = {}\n\t" +
                      "loginUrl {}", appName, loginUrl);
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
        st.add("app_name", appName);
        st.add("login_url", convertRelativeUrl(req.getContextPath(), loginUrl));
        return st.render();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        res.getWriter().print(generatePageText(req));
    }
}
