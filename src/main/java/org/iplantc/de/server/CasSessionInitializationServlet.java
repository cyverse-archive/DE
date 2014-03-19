package org.iplantc.de.server;

import static org.iplantc.de.server.util.CasUtils.attributePrincipalFromServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.client.authentication.AttributePrincipal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A servlet used to initialize HTTP sessions for CAS-secured web applications.
 *
 * @author Dennis Roberts
 */
public class CasSessionInitializationServlet extends HttpServlet {

    private static final long serialVersionUID = 9108716044066000369L;

    /**
     * Used to log debugging information.
     */
    private static final Logger LOG = Logger.getLogger(CasSessionInitializationServlet.class);

    /**
     * The name of the HTTP session attribute used to store the CAS principal.
     */
    private static final String CAS_PRINCIPAL_ATTR = "casPrincipal";

    /**
     * The domain name to use for the EPPN.
     *
     * TODO: this will have to go away when we federate.
     */
    private static final String EPPN_DOMAIN_NAME = "@iplantcollaborative.org";

    /**
     * The names of the HTTP session attribute used to store the remote username.
     */
    private static final String[] CAS_USERNAME_ATTRS = {"username", DESecurityConstants.LOCAL_SHIB_UID};

    /**
     * A map that translates CAS user attribute names to the names used by the DE.
     */
    private static final Map<String, String> ATTR_NAME_MAP = new HashMap<String, String>();

    static {
        ATTR_NAME_MAP.put("email", DESecurityConstants.LOCAL_SHIB_MAIL);
    }

    /**
     * The page to redirect the user to after initializing the session.
     */
    private String initialPage;

    /**
     * The default constructor.
     */
    public CasSessionInitializationServlet() {}

    /**
     * @param initialPage the page to redirect the user to after initializing the session.
     */
    public CasSessionInitializationServlet(String initialPage) {
        this.initialPage = initialPage;
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException  if the {@code initialPage} initialization parameter is not defined.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (initialPage == null) {
            initialPage = getServletConfig().getInitParameter("initialPage");
            if (initialPage == null) {
                throw new ServletException("initialPage initialization parameter required");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            copyCasAttributes(req);
            redirectUser(req, resp);
        }
        catch (Exception e) {
            LOG.error("unable to initialize the user's session", e);
            resp.setContentType("text/plain");
            resp.getWriter().println(e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Copies the attributes from the CAS Attribute Principal into the user's session. some attributes have an alternate
     * name for backward compatibility. Those attributes will be stored under both the original name and the alternate
     * name.
     *
     * @param req the HTTP servlet request.
     */
    private void copyCasAttributes(HttpServletRequest req) {
        HttpSession session = req.getSession();
        storeUsername(session, req.getRemoteUser());
        AttributePrincipal principal = attributePrincipalFromServletRequest(req);
        Map<String, Object> attrs = principal.getAttributes();
        for (String name : attrs.keySet()) {
            Object value = attrs.get(name);
            setAttr(session, name, value);
            String alternateName = ATTR_NAME_MAP.get(name);
            if (alternateName != null) {
                setAttr(session, alternateName, value);
            }
        }
    }

    /**
     * Stores the username in all of the session attributes that expect the username.
     *
     * @param session the HTTP session.
     * @param remoteUser the username.
     */
    private void storeUsername(HttpSession session, String remoteUser) {
        setAttr(session, CAS_PRINCIPAL_ATTR, remoteUser);
        setAttr(session, DESecurityConstants.LOCAL_SHIB_EPPN, remoteUser + EPPN_DOMAIN_NAME);
        for (String name : CAS_USERNAME_ATTRS) {
            setAttr(session, name, remoteUser);
        }
    }

    /**
     * Sets an attribute in an HTTP session, logging the attribute value if debugging is enabled.
     *
     * @param session the HTTP session.
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     */
    private void setAttr(HttpSession session, String name, Object value) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Saving user attribute: " + name + " = " + value);
        }
        session.setAttribute(name, value);
    }

    /**
     * Redirects the user to the web application's initial page.
     *
     * @param req the HTTP servlet request.
     * @param resp the HTTP servlet response.
     * @throws IOException if the redirect can't be sent to the client.
     */
    private void redirectUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuffer urlBuffer = req.getRequestURL();
        int pos = StringUtils.lastOrdinalIndexOf(urlBuffer.toString(), "/", 2);
        urlBuffer.delete(pos + 1, urlBuffer.length());
        urlBuffer.append(initialPage);
        resp.sendRedirect(urlBuffer.toString());
    }
}
