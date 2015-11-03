package org.iplantc.de.shared.services;

import org.iplantc.de.shared.AsyncCallbackWrapper;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import java.util.HashMap;

/**
 * This class is intended to be used for all DE API service calls.
 * It simply wraps the given {@code AsyncCallback} in an {@code AsyncCallbackWrapper}, which performs
 * common operations for DE API and auth-related transactions.
 *
 * @author jstroot
 */
public class DiscEnvApiService {

    @Inject DEServiceAsync deService;

    @Inject
    public DiscEnvApiService() {
    }

    public Request getServiceData(ServiceCallWrapper wrapper,
                                  AsyncCallback<String> callback) {
        return deService.getServiceData(wrapper,
                                        new AsyncCallbackWrapper<>(callback));
    }

    public Request getServiceData(ServiceCallWrapper wrapper,
                                  HashMap<String, String> mdcMap,
                                  AsyncCallback<String> callback) {
        return deService.getServiceData(wrapper,
                                        mdcMap,
                                        new AsyncCallbackWrapper<>(callback));
    }
}
