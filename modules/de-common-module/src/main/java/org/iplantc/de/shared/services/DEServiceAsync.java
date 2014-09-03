package org.iplantc.de.shared.services;

import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Defines an interface for all asynchronous remote services implemented in the application.
 */
public interface DEServiceAsync {
    Request getServiceData(ServiceCallWrapper wrapper, AsyncCallback<String> callback);

    Request getServiceData(MultiPartServiceWrapper wrapper, AsyncCallback<String> callback);
}
