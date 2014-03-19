package org.iplantc.de.server;

import static org.iplantc.de.server.util.ServletUtils.getPropertyPrefix;
import static org.iplantc.de.server.util.ServletUtils.getRequiredProp;
import static org.iplantc.de.server.util.ServletUtils.loadResource;
import static org.iplantc.de.server.util.UrlUtils.convertRelativeUrl;

import org.iplantc.clavin.spring.ConfigAliasResolver;

import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A shared servlet for handling CAS authorization failure.
 *
 * @author Dennis Roberts
 */
public class CasAccessDeniedServlet extends HttpServlet {

    /**
     * The name of the property containing the relative URL to redirect the user to when the user chooses to log out of
     * all applications.
     */
    private static final String LOGOUT_URL_PROPERTY = ".cas.logout-url";

    /**
     * The name of the template used to generate the HTML to return.
     */
    private static final String TEMPLATE_NAME = "access-denied-template.html";

    /**
     * The URL used to log out of the application,
     */
    private String logoutUrl;

    /**
     * The text of the template used to generate the HTML to return.
     */
    private String templateText;

    /**
     * True if the template has been initialized.
     */
    private Boolean initialized = false;

    /**
     * The default constructor.
     */
    public CasAccessDeniedServlet() {
    }

    /**
     * @param props the web application configuration properties.
     * @param propPrefix the property name prefix.
     */
    public CasAccessDeniedServlet(Properties props, String propPrefix) {
        loadConfig(props, propPrefix);
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if a required configuration setting is missing.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (!initialized) {
            Properties config = ConfigAliasResolver.getRequiredAliasedConfigFrom(getServletContext(), "webapp");
            loadConfig(config, getPropertyPrefix(getServletConfig()));
         }
    }

    /**
     * Loads the configuration from the configuration properties.
     *
     * @param config the configuration properties.
     * @param propPrefix the property name prefix.
     */
    private void loadConfig(Properties config, String propPrefix) {
        logoutUrl = getRequiredProp(config, propPrefix + LOGOUT_URL_PROPERTY);
        templateText = loadResource(TEMPLATE_NAME);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        res.getWriter().print(generatePageText(req));
    }
}
