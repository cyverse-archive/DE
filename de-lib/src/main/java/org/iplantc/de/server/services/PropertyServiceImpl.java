package org.iplantc.de.server.services;

import org.iplantc.de.shared.services.PropertyService;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author jstroot
 */
public class PropertyServiceImpl extends RemoteServiceServlet implements PropertyService{

    private static final long serialVersionUID = 1L;

    /**
     * The configuration settings.
     */
    @Autowired
    @Qualifier("deProperties")
    private Properties props;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    public Map<String, String> getProperties() throws SerializationException {
        HashMap<String, String> propertyMap = new HashMap<>();
        for (Object key : props.keySet()) {
            propertyMap.put(key.toString(), props.get(key).toString());
        }
        return propertyMap;
    }
}
