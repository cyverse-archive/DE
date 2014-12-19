package org.iplantc.de.admin.desktop.client.presenter;

import org.iplantc.de.admin.desktop.client.views.BelphegorView;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class BelphegorPresenterImpl implements BelphegorView.Presenter {

    private final BelphegorView view;

    @Inject
    public BelphegorPresenterImpl(BelphegorView view) {
        this.view = view;
    }

    @Override
    public void go(HasWidgets hasWidgets) {

        hasWidgets.add(view.asWidget());
    }

}
