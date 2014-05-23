package org.iplantc.de.server;

import javax.servlet.ServletException;

public class CasOAuthCallbackServlet  extends OAuthCallbackServlet {

    public CasOAuthCallbackServlet() {
        super();
        setUrlConnector(new CasUrlConnector());
    }

    public CasOAuthCallbackServlet(final DiscoveryEnvironmentProperties deProps) {
        super();
        setUrlConnector(new CasUrlConnector());
        setDeProps(deProps);
    }
}
