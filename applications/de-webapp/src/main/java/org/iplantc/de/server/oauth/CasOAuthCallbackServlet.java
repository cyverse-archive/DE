package org.iplantc.de.server.oauth;

import org.iplantc.de.server.auth.CasUrlConnector;

import javax.inject.Named;

/**
 * @author jstroot
 * @author Dennis Roberts
 */
@Named("oauthCallbackServlet")
public class CasOAuthCallbackServlet  extends OAuthCallbackServlet {

    public CasOAuthCallbackServlet() {
        super();
        setUrlConnector(new CasUrlConnector());
    }
}
