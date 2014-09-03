package org.iplantc.admin.belphegor.server.services;

import org.iplantc.admin.belphegor.server.BelphegorProperties;
import org.iplantc.admin.belphegor.shared.services.BelphegorPropertyService;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author jstroot
 */
public class BelphegorPropertyServiceImpl extends RemoteServiceServlet implements BelphegorPropertyService {

    private Properties props;

    @Override
    public Map<String, String> getProperties() throws SerializationException {
        HashMap<String, String> propertyMap = new HashMap<>();
        for (Object key : props.keySet()) {
            propertyMap.put(key.toString(), props.get(key).toString());
        }
        return propertyMap;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (props == null) {
            try {
                BelphegorProperties belphegorProperties = BelphegorProperties.get();
                props = belphegorProperties.getProperties();
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }
    }
}