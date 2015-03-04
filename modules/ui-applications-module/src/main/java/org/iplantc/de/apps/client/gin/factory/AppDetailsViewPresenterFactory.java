package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.apps.client.AppsView;

/**
 * Created by jstroot on 3/4/15.
 * @author jstroot
 */
public interface AppDetailsViewPresenterFactory {
    AppDetailsView.Presenter create(AppsView.Presenter appsViewPresenter);
}
