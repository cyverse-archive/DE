package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;

import java.util.Map;

/**
 * Provides a service contract that can be used to obtain discovery environment configuration properties.
 * 
 * @author Dennis Roberts
 */
public interface PropertyService extends RemoteService {
    /**
     * Retrieves the entire set of discovery environment properties.
     * 
     * @return the set of discovery environment properties.
     * @throws SerializationException if the properties can't be retrieved.
     */
    Map<String, String> getProperties() throws SerializationException;
}
