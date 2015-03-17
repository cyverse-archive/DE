package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.services.DEFeedbackServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.Splittable;

public class DEFeedbackServiceFacadeStub implements DEFeedbackServiceFacade {
    @Override
    public void submitFeedback(Splittable feedback, AsyncCallback<Void> callback) {

    }
}
