package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

import java.util.HashMap;

/**
 * Provides a service contract that can be used to obtain discovery environment configuration properties.
 * 
 * @author Dennis Roberts
 * @author jstroot
 */
@RemoteServiceRelativePath("properties.rpc")
public interface PropertyService extends RemoteService {
    /**
     * Retrieves the entire set of discovery environment properties.
     * 
     * @return the set of discovery environment properties.
     * @throws SerializationException if the properties can't be retrieved.
     */
    HashMap<String, String> getProperties() throws SerializationException;
}
