package org.iplantc.de.shared;

import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Defines an interface for all remote services implemented in the application.
 */
public interface DEService extends RemoteService {
    String getServiceData(ServiceCallWrapper wrapper) throws SerializationException, AuthenticationException;

    String getServiceData(MultiPartServiceWrapper wrapper) throws SerializationException, AuthenticationException;
}
