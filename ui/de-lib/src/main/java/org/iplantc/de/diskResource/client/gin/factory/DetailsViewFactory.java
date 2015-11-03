package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.diskResource.client.DetailsView;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public interface DetailsViewFactory {
    DetailsView create(DetailsView.Presenter presenter);
}
