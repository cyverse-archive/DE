package org.iplantc.de.server;

import org.iplantc.clavin.spring.ConfigAliasResolver;
import org.iplantc.de.shared.services.PropertyService;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;

public class PropertyServlet extends RemoteServiceServlet implements PropertyService{

    /**
     * {@inheritDoc}
     */
    private static final long serialVersionUID = 1L;

    /**
     * The configuration settings.
     */
    private Properties props;

    /**
     * The default constructor.
     */
    public PropertyServlet() {}

    /**
     * @param props the configuration properties.
     */
    public PropertyServlet(Properties props) {
        this.props = props;
    }

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
            props = ConfigAliasResolver.getRequiredAliasedConfigFrom(getServletContext(), "webapp");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getProperties() throws SerializationException {
        HashMap<String, String> propertyMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            propertyMap.put(key.toString(), props.get(key).toString());
        }
        return propertyMap;
    }
}
