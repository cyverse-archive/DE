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
 * A shared servlet for handling CAS logout.
 *
 * @author Dennis Roberts, jstroot
 */
public class CasLogoutServlet extends HttpServlet implements HttpRequestHandler {

    private static final long serialVersionUID = 4844593776560973333L;
    private final Logger LOG = LoggerFactory.getLogger(CasLogoutServlet.class);

    /**
     * The name of the file containing the template for the logout alert page.
     */
    private static final String TEMPLATE_FILENAME = "logout-alert-template.html";

    /**
     * The URL to redirect the user to when the user chooses to log out of all applications.
     */
    private String logoutUrl;

    /**
     * The name of the current web application.
     */
    private String appName;

    /**
     * The URL to redirect the user to when the user chooses to log back into the current web application.
     */
    private String loginUrl;

    /**
     * The URL to redirect the user to when the user chooses not to log out of all applications.
     */
    private String noLogoutUrl;

    /**
     * Text that describes all of the applications whose sessions will be closed if the user chooses to log out of all
     * applications. This could be a simple phrase such as "all applications" or it could be an actual list of
     * application names.
     */
    private String appList;

    /**
     * The text of the page template to return.
     */
    private String templateText;

    /**
     * The default constructor.
     */
    public CasLogoutServlet() {}

    public CasLogoutServlet(final String logoutUrl,
                            final String appName,
                            final String loginUrl,
                            final String noLogoutUrl,
                            final String appList){

        this.logoutUrl = logoutUrl;
        this.appName = appName;
        this.loginUrl = loginUrl;
        this.noLogoutUrl = noLogoutUrl;
        this.appList = appList;
        templateText = loadResource(TEMPLATE_FILENAME);
        if(templateText == null){
            System.out.println("template text is null");
        }
        LOG.debug("Constructor args:\n\t" +
                      "logoutUrl = {}\n\t" +
                      "appName = {}\n\t" +
                      "loginUrl = {}\n\t" +
                      "noLogoutUrl = {}\n\t" +
                      "appList = {}", logoutUrl, appName, loginUrl, noLogoutUrl, appList);
    }

    @Override
    public void handleRequest(HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
        if(request.getMethod().equalsIgnoreCase("GET")){
            doGet(request, response);
        }
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.getWriter().print(generatePageText(req));
    }
}
