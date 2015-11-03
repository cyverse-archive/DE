package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provides an asynchronous service contract for AboutApplicationService.
 * 
 * @see org.iplantc.de.client.services.AboutApplicationService
 * @author lenards
 * 
 */
public interface AboutApplicationServiceAsync {
    /**
     * Fetch information about the application.
     * 
     * The information return will be in JSON format.
     * 
     * @param callback the callback to be executed upon service execution
     */
    void getAboutInfo(AsyncCallback<String> callback);

}
