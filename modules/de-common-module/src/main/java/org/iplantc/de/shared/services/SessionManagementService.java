package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;

import java.util.Map;

/**
 * Contract specifying what client session management services shall support.
 * 
 * @author sriram
 * 
 */
public interface SessionManagementService extends RemoteService {
    /**
     * Obtains the user session attributes as a map.
     * 
     * @return the map of attribute names to attribute values.
     * @throws SerializationException if an error occurs.
     */
    Map<String, String> getAttributes() throws SerializationException;

    /**
     * Retrieve an individual attribute.
     * 
     * @param key attribute key.
     * @return attribute value.
     * @throws SerializationException if an error occurs.
     */
    String getAttribute(String key) throws SerializationException;

    /**
     * Set an individual attribute.
     * 
     * @param key attribute key.
     * @param value attribute value.
     * @throws SerializationException if an error occurs.
     */
    void setAttribute(String key, String value) throws SerializationException;

    /**
     * Remove an attribute.
     * 
     * @param key attribute key.
     * @throws SerializationException if an error occurs.
     */
    void removeAttribute(String key) throws SerializationException;

    /**
     * Invalidate session.
     * 
     * @throws SerializationException if an error occurs.
     */
    void invalidate() throws SerializationException;
}
