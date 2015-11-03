package org.iplantc.de.apps.client.presenter.callbacks;

import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jstroot
 */
public class RateAppCallback implements AsyncCallback<AppFeedback> {
    private final App appToRate;
    private final EventBus eventBus;

    public RateAppCallback(final App appToRate,
                           final EventBus eventBus) {
        this.appToRate = appToRate;
        this.eventBus = eventBus;
    }

    @Override
    public void onFailure(Throwable caught) {
        ErrorHandler.post(caught);
    }

    @Override
    public void onSuccess(AppFeedback result) {
        appToRate.setRating(result);

        eventBus.fireEvent(new AppUpdatedEvent(appToRate));
    }
}
