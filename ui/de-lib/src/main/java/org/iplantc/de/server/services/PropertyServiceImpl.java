package org.iplantc.de.server.services;

import org.iplantc.de.shared.DEProperties;
import org.iplantc.de.shared.services.PropertyService;

import com.google.gwt.user.client.rpc.SerializationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author jstroot
 */
public class PropertyServiceImpl implements PropertyService{

//    private static final long serialVersionUID = 1L;

    /**
     * The configuration settings.
     */
    private final Environment environment;

    private final Logger LOG = LoggerFactory.getLogger(PropertyServiceImpl.class);

    public PropertyServiceImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public HashMap<String, String> getProperties() throws SerializationException {
        List<String> uikeys = DEProperties.getInstance().getPropertyList();
        LOG.error("^^^^^^^property list ^^^^^^^ ---->" + uikeys);
        LOG.error("^^^^^^^property list size ^^^^^^^ ---->" + uikeys.size());


        HashMap<String, Object> propertyMap = new HashMap<>();
        for(Iterator it = ((AbstractEnvironment) environment).getPropertySources().iterator(); it.hasNext(); ) {
            PropertySource propertySource = (PropertySource) it.next();
            if (propertySource instanceof MapPropertySource) {
                propertyMap.putAll(((MapPropertySource) propertySource).getSource());
            }
        }

        HashMap<String, String> stringProps = new HashMap<>();
        for (String key : uikeys) {
            stringProps.put(key, propertyMap.get(key).toString());
        }
        return stringProps;
    }
}
