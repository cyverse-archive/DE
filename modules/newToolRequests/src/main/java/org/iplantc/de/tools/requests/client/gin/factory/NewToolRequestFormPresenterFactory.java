package org.iplantc.de.tools.requests.client.gin.factory;

import org.iplantc.de.tools.requests.client.views.NewToolRequestFormView;

import com.google.gwt.user.client.Command;

/**
 * @author jstroot
 */
public interface NewToolRequestFormPresenterFactory {
    NewToolRequestFormView.Presenter createPresenter(NewToolRequestFormView view,
                                                     Command callbackCommand);
}
