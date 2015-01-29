package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;

import java.util.List;

/**
 * Created by jstroot on 1/27/15.
 * @author jstroot
 */
public interface GridViewPresenterFactory {
    GridView.Presenter create(NavigationView.Presenter navigationPresenter,
                              List<InfoType> infoTypeFilters,
                              TYPE entityType);
}
