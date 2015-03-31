package org.iplantc.de.server.auth;

import static org.iplantc.de.server.util.ServletUtils.loadResource;
import static org.iplantc.de.server.util.UrlUtils.convertRelativeUrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.stringtemplate.v4.ST;

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A shared servlet for handling CAS logout.
 *
 * @author Dennis Roberts, jstroot
 */
@Named("logoutSuccessServlet")
public class CasLogoutServlet extends HttpServlet implements HttpRequestHandler {

    private static final long serialVersionUID = 4844593776560973333L;

    /**
     * The name of the file containing the template for the logout alert page.
     */
    private static final String TEMPLATE_FILENAME = "logout-alert-template.html";

    private final Logger LOG = LoggerFactory.getLogger(CasLogoutServlet.class);

    /**
     * Text that describes all of the apps whose sessions will be closed if the user chooses to log out of all
     * apps. This could be a simple phrase such as "all apps" or it could be an actual list of
     * application names.
     */
    private String appList;
    /**
     * The name of the current web application.
     */
    private String appName;

    /**
     * The URL to redirect the user to when the user chooses to log back into the current web application.
     */
    private String loginUrl;
    /**
     * The URL to redirect the user to when the user chooses to log out of all apps.
     */
    private String logoutUrl;
    /**
     * The URL to redirect the user to when the user chooses not to log out of all apps.
     */
    private String noLogoutUrl;
    /**
     * The text of the page template to return.
     */
    private String templateText;

    /**
     * The default constructor.
     */
    public CasLogoutServlet() {
        templateText = loadResource(TEMPLATE_FILENAME);
        if (templateText == null) {
            LOG.info("template text is null");
        }
    }

    @Override
    public void handleRequest(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            doGet(request, response);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Value("${org.iplantc.discoveryenvironment.cas.app-list}")
    public void setAppList(String appList) {
        this.appList = appList;
        LOG.trace("Set appList = {}", appList);
    }

    @Value("${org.iplantc.discoveryenvironment.cas.app-name}")
    public void setAppName(String appName) {
        this.appName = appName;
        LOG.trace("Set appName = {}", appName);
    }

    @Value("${org.iplantc.discoveryenvironment.cas.login-url}")
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
        LOG.trace("Set loginUrl = {}", loginUrl);
    }

    @Value("${org.iplantc.discoveryenvironment.cas.logout-url}")
    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
        LOG.trace("Set logoutUrl = {}", logoutUrl);
    }

    @Value("${org.iplantc.discoveryenvironment.cas.no-logout-url}")
    public void setNoLogoutUrl(String noLogoutUrl) {
        this.noLogoutUrl = noLogoutUrl;
        LOG.trace("Set noLogoutUrl = {}", noLogoutUrl);
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.getWriter().print(generatePageText(req));
    }

    /**
     * Generates the text of the page that will be returned by this servlet. The page text itself is static for any
     * given web application, so it only needs to be generated once upon startup.
     *
     * @throws ServletException if the page text can't be generated.
     */
    private String generatePageText(HttpServletRequest req) throws ServletException {
        ST st = new ST(templateText, '$', '$');
        st.add("logout_url", convertRelativeUrl(req.getContextPath(), logoutUrl));
        st.add("app_name", appName);
        st.add("login_url", convertRelativeUrl(req.getContextPath(), loginUrl));
        st.add("no_logout_url", convertRelativeUrl(req.getContextPath(), noLogoutUrl));
        st.add("app_list", appList);
        return st.render();
    }
}
