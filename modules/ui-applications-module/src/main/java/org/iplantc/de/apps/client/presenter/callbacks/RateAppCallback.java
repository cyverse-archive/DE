package org.iplantc.de.apps.client.presenter.callbacks;

import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author jstroot
 */
public class RateAppCallback implements AsyncCallback<String> {
    private final App appToRate;
    private final AppRatingSelected event;
    private final EventBus eventBus;

    public RateAppCallback(final App appToRate,
                           final AppRatingSelected event,
                           final EventBus eventBus) {
        this.appToRate = appToRate;
        this.event = event;
        this.eventBus = eventBus;
    }

    @Override
    public void onFailure(Throwable caught) {
        ErrorHandler.post(caught);
    }

    @Override
    public void onSuccess(String result) {
        appToRate.getRating().setUserRating(event.getScore());


        eventBus.fireEvent(new AppUpdatedEvent(appToRate));
    }
}
