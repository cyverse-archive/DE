package org.iplantc.de.apps.client.presenter.toolBar;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;

/**
 * TODO Search will stay here until it is necessary to fold it out
 * @author jstroot
 */
public class AppsToolbarPresenterImpl implements AppsToolbarView.Presenter {

    private final AppsToolbarView view;
    private final AppSearchRpcProxy proxy;

    public AppsToolbarPresenterImpl() {
        proxy = null;
        view = null;
    }
}
