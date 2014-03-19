package org.iplantc.de.client.preferences.presenter;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.preferences.views.PreferencesView;
import org.iplantc.de.client.preferences.views.PreferencesView.Presenter;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;

/**
 * 
 * A presenter impl for user preferences
 * 
 * @author sriram
 * 
 */
public class PreferencesPresenterImpl implements Presenter {

    PreferencesView view;

    public PreferencesPresenterImpl(PreferencesView view) {
        this.view = view;
        this.view.setPresenter(this);
        this.view.setValues();
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
    }

    @Override
    public boolean validateAndSave() {
        if (view.isValid()) {
            UserSettings us = view.getValues();
            ServicesInjector.INSTANCE.getUserSessionServiceFacade().saveUserPreferences(us.asSplittable(), new AsyncCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(I18N.DISPLAY.saveSettings()));
                }

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);
                }
            });
        }

        return view.isValid();
    }

    @Override
    public void setDefaults() {
        view.setDefaultValues();
    }

}
