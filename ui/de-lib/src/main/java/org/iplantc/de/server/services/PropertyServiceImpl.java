package org.iplantc.de.server.services;

import org.iplantc.de.shared.services.PropertyService;

import com.google.gwt.user.client.rpc.SerializationException;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author jstroot
 */
public class PropertyServiceImpl implements PropertyService{

//    private static final long serialVersionUID = 1L;

    /**
     * The configuration settings.
     */
    private final Environment environment;

    public PropertyServiceImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public HashMap<String, String> getProperties() throws SerializationException {
        HashMap<String, Object> propertyMap = new HashMap<>();
        for(Iterator it = ((AbstractEnvironment) environment).getPropertySources().iterator(); it.hasNext(); ) {
            PropertySource propertySource = (PropertySource) it.next();
            if (propertySource instanceof MapPropertySource) {
                propertyMap.putAll(((MapPropertySource) propertySource).getSource());
            }
        }

        HashMap<String, String> stringProps = new HashMap<>();
        for (Object key : propertyMap.keySet()) {
            if(propertyMap.get(key) instanceof String){
                stringProps.put(key.toString(), propertyMap.get(key).toString());
            }
        }
        return stringProps;
    }
}
