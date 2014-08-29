package org.iplantc.de.server.service;

import org.iplantc.de.server.DiscoveryEnvironmentProperties;
import org.iplantc.de.shared.services.PropertyService;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;

public class PropertyServiceImpl extends RemoteServiceServlet implements PropertyService{

    private static final long serialVersionUID = 1L;

    /**
     * The configuration settings.
     */
    private Properties props;

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if the configuration properties can't be loaded.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (props == null) {
            try {
                DiscoveryEnvironmentProperties deProps = DiscoveryEnvironmentProperties.getDiscoveryEnvironmentProperties();
                props = deProps.getProperties();
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getProperties() throws SerializationException {
        HashMap<String, String> propertyMap = new HashMap<>();
        for (Object key : props.keySet()) {
            propertyMap.put(key.toString(), props.get(key).toString());
        }
        return propertyMap;
    }
}
