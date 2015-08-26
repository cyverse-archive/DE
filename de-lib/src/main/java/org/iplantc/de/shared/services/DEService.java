package org.iplantc.de.shared.services;

import org.iplantc.de.shared.exceptions.AuthenticationException;
import org.iplantc.de.shared.exceptions.HttpException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;

import java.util.HashMap;

/**
 * Defines an interface for all remote services implemented in the application.
 *
 * @author jstroot
 */
@RemoteServiceRelativePath("api.rpc")
public interface DEService extends RemoteService {
    String getServiceData(ServiceCallWrapper wrapper) throws SerializationException, AuthenticationException,
                                                             HttpException;

    /**
     * Allows the client to send extra information which will be added to the
     * logger's {@code MDC}
     * @param wrapper
     * @param extraLoggerMdcItems
     * @return
     * @throws SerializationException
     * @throws AuthenticationException
     * @throws HttpException
     */
    String getServiceData(ServiceCallWrapper wrapper,
                          HashMap<String, String> extraLoggerMdcItems) throws SerializationException, AuthenticationException,
                                                                              HttpException;

}
