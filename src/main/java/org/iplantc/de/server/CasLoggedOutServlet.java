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
 * A shared servlet for displaying a logout-successful page.
 *
 * @author Dennis Roberts
 */
public class CasLoggedOutServlet extends HttpServlet {

    /**
     * The name of the property used to retrieve the application name to display in the logout success page.
     */
    private static final String APP_NAME_PROPERTY = ".cas.app-name";

    /**
     * The name of the property used to retrieve the login URL to use in the logout success page.
     */
    private static final String LOGIN_URL_PROPERTY = ".cas.login-url";

    /**
     * The name of the template used to generate the logout success page.
     */
    private static final String TEMPLATE_NAME = "logged-out-template.html";

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
     * True if the servlet has been initialized.
     */
    private boolean initialized = false;

    /**
     * The default constructor.
     */
    public CasLoggedOutServlet() {
    }

    /**
     * @param props the web application configuration properties.
     * @param propPrefix the property name prefix.
     */
    public CasLoggedOutServlet(Properties props, String propPrefix) {
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
        appName = getRequiredProp(config, propPrefix + APP_NAME_PROPERTY);
        loginUrl = getRequiredProp(config, propPrefix + LOGIN_URL_PROPERTY);
        templateText = loadResource(TEMPLATE_NAME);
        initialized = true;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        res.getWriter().print(generatePageText(req));
    }
}
