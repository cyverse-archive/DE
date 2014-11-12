package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.views.NewToolRequestFormView;

import com.google.gwt.user.client.Command;

/**
 * @author jstroot
 */
public interface NewToolRequestFormPresenterFactory {
    NewToolRequestFormView.Presenter createPresenter(NewToolRequestFormView view,
                                                     Command callbackCommand);
}
