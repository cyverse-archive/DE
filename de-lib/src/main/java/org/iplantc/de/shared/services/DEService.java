package org.iplantc.de.shared.services;

import org.iplantc.de.shared.exceptions.AuthenticationException;
import org.iplantc.de.shared.exceptions.HttpException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Defines an interface for all remote services implemented in the application.
 *
 * @author jstroot
 */
@RemoteServiceRelativePath("deservice")
public interface DEService extends RemoteService {
    String getServiceData(ServiceCallWrapper wrapper) throws SerializationException, AuthenticationException,
                                                             HttpException;

}
