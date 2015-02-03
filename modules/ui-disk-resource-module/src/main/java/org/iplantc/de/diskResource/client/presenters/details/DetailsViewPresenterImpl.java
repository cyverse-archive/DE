package org.iplantc.de.diskResource.client.presenters.details;

import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.DetailsView;
import org.iplantc.de.diskResource.client.gin.factory.DetailsViewFactory;

import com.google.inject.Inject;

/**
 * @author jstroot
 */
public class DetailsViewPresenterImpl implements DetailsView.Presenter {

    @Inject DiskResourceServiceFacade diskResourceService;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject DiskResourceAutoBeanFactory drFactory;
    private final DetailsView view;

    @Inject
    DetailsViewPresenterImpl(final DetailsViewFactory viewFactory) {
        this.view = viewFactory.create(this);
    }

    @Override
    public DetailsView getView() {
        return view;
    }


}
