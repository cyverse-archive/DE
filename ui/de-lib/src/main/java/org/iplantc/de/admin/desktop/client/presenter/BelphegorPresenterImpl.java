package org.iplantc.de.admin.desktop.client.presenter;

import org.iplantc.de.admin.desktop.client.views.BelphegorView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.shared.events.UserLoggedOutEvent;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class BelphegorPresenterImpl implements BelphegorView.Presenter,
                                               UserLoggedOutEvent.UserLoggedOutEventHandler {

    private final BelphegorView view;

    @Inject
    public BelphegorPresenterImpl(BelphegorView view) {
        this.view = view;
        EventBus.getInstance().addHandler(UserLoggedOutEvent.TYPE, this);
    }

    @Override
    public void go(HasWidgets hasWidgets) {
       hasWidgets.add(view.asWidget());
    }

    @Override
    public void OnLoggedOut(UserLoggedOutEvent event) {
        GWT.log("belphegor on logged out...");
        view.doLogout();
    }
}
