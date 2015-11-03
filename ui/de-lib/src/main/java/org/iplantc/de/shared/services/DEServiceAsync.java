package org.iplantc.de.shared.services;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.HashMap;

/**
 * Defines an interface for all asynchronous remote services implemented in the application.
 *
 * @author jstroot
 */
public interface DEServiceAsync {
    Request getServiceData(ServiceCallWrapper wrapper,
                           AsyncCallback<String> callback);

    Request getServiceData(ServiceCallWrapper wrapper,
                           HashMap<String, String> extraLoggerMdcItems,
                           AsyncCallback<String> async);
}
