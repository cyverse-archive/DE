/**
 * 
 */
package org.iplantc.de.client.preferences.views;

import org.iplantc.de.client.models.UserSettings;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author sriram
 * 
 */
public interface PreferencesView extends IsWidget {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        boolean validateAndSave();

        void setDefaults();

    }

    void setPresenter(Presenter p);

    void setDefaultValues();

    void setValues();

    boolean isValid();

    UserSettings getValues();
}
