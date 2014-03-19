package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DEFeedbackServiceFacade {

    /**
     * Submits Discovery Environment feedback on behalf of the user.
     * 
     * @param feedback the feedback in the form of a JSON object.
     * @param callback executed when the RPC call completes.
     */
    void submitFeedback(String feedback, AsyncCallback<String> callback);

}