package org.iplantc.de.server.util;

import org.apache.log4j.Logger;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.security.cas.authentication.CasAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility methods for dealing with CAS.
 * 
 * @author Dennis Roberts
 */
public class CasUtils {

    /**
     * Used to log debugging information.
     */
    private static final Logger LOG = Logger.getLogger(CasUtils.class);

    /**
     * Prevent instantiation.
     */
    private CasUtils() {
    }
    
    /**
     * Obtains an AttributePrincipal object from an HTTP servlet request.  The user must have been authenticated
     * either directly via the Java CAS client or indirectly via the Java CAS client and Spring Security.
     * 
     * @param req the HTTP servlet request.
     * @return the attribute principal or null if the user isn't authenticated.
     */
    public static AttributePrincipal attributePrincipalFromServletRequest(HttpServletRequest req) {
        return attributePrincipalFromUserPrincipal(req.getUserPrincipal());
    }

    /**
     * Obtains an AttributePrincipal object from a general user principal object.  The user must have been authenticated
     * either directly via the Java CAS client or indirectly via the Java CAS client and Spring Security.
     * 
     * @param userPrincipal the user principal object.
     * @return the attribute principal or null if the user isn't authenticated.
     */
    public static AttributePrincipal attributePrincipalFromUserPrincipal(Object userPrincipal) {
        if (userPrincipal instanceof AttributePrincipal) {
            LOG.debug("returning the top-level user principal");
            return (AttributePrincipal) userPrincipal;
        }
        else if (userPrincipal instanceof CasAuthenticationToken) {
            LOG.debug("returning the user principal from within the CAS assertion");
            return (AttributePrincipal) ((CasAuthenticationToken) userPrincipal).getAssertion().getPrincipal();
        }
        return null;
    }
}
