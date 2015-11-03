package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.ToolbarView;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public interface ToolbarViewPresenterFactory {
    ToolbarView.Presenter create(DiskResourceView.Presenter parentPresenter);
}
