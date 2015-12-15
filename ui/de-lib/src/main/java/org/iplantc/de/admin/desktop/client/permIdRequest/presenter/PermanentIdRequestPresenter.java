package org.iplantc.de.admin.desktop.client.permIdRequest.presenter;

import org.iplantc.de.admin.desktop.client.permIdRequest.view.PermIdRequestView;
import org.iplantc.de.admin.desktop.client.permIdRequest.view.PermIdRequestView.Presenter;
import org.iplantc.de.admin.desktop.client.permIdRequest.view.PermIdRequestViewImpl;
import org.iplantc.de.admin.desktop.client.permIdRequest.view.PermanentIdRequestProperties;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HasOneWidget;

/**
 * 
 * 
 * @author sriram
 * 
 */
public class PermanentIdRequestPresenter implements Presenter {

    PermIdRequestView view;

    public PermanentIdRequestPresenter() {
        PermanentIdRequestProperties props = GWT.create(PermanentIdRequestProperties.class);
        view = new PermIdRequestViewImpl(props);
    }

    @Override
    public void fetchMetadata() {
        // TODO Auto-generated method stub

    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void getPermIdRequests() {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadPermIdRequests() {
        // TODO Auto-generated method stub

    }

}
