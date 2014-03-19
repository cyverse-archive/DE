package org.iplantc.de.client.desktop.views;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.IsWidget;

public interface DEFeedbackView extends IsWidget {

    public boolean validate();

    public JSONObject toJson();

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        void validateAndSubmit();

    }
}
