package org.iplantc.de.shared.services;

import org.iplantc.de.shared.AuthenticationException;
import org.iplantc.de.shared.HttpException;
import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Defines an interface for all remote services implemented in the application.
 */
@RemoteServiceRelativePath("deservice")
public interface DEService extends RemoteService {
    String getServiceData(ServiceCallWrapper wrapper) throws SerializationException, AuthenticationException,
                                                             HttpException;

    String getServiceData(MultiPartServiceWrapper wrapper) throws SerializationException, AuthenticationException,
            HttpException;
}
