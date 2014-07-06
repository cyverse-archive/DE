package org.iplantc.de.client.newDesktop.views.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by jstroot on 7/10/14.
 */
public class UserSettingsView {
    interface UserPreferencesViewUiBinder extends UiBinder<HTMLPanel, UserSettingsView> {
    }

    private static UserPreferencesViewUiBinder ourUiBinder = GWT.create(UserPreferencesViewUiBinder.class);

    public UserSettingsView() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);

    }
}