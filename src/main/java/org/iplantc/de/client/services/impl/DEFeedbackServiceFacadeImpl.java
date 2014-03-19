package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PUT;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.services.DEFeedbackServiceFacade;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * Provides access to remote services for submitting user feedback.
 */
@SuppressWarnings("nls")
public class DEFeedbackServiceFacadeImpl implements DEFeedbackServiceFacade {

    private static String FEEDBACK_SERVICE_PATH = "feedback";
    private final DEProperties deProperties;
    private final DEServiceFacade deServiceFacade;

    @Inject
    public DEFeedbackServiceFacadeImpl(final DEServiceFacade deServiceFacade, final DEProperties deProperties) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.DEFeedbackServiceFacade#submitFeedback(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void submitFeedback(String feedback, AsyncCallback<String> callback) {
        String addr = deProperties.getMuleServiceBaseUrl() + FEEDBACK_SERVICE_PATH;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, addr, feedback);
        deServiceFacade.getServiceData(wrapper, callback);
    }
}
