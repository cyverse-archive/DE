package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;

/**
 * @author jstroot
 */
public interface DiskResourceViewFactory {
    DiskResourceView create(DiskResourceView.Presenter presenter,
                            NavigationView.Presenter navigationPresenter,
                            GridView.Presenter gridViewPresenter);
}
