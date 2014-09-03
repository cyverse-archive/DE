package org.iplantc.de.server.oauth;

import org.iplantc.de.server.auth.CasUrlConnector;
import org.iplantc.de.server.DiscoveryEnvironmentProperties;

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
