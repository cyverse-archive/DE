package org.iplantc.de.diskResource.client.presenters.details;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.gin.factory.DetailsViewFactory;

import com.google.inject.Inject;

import java.util.logging.Logger;

/**
 * @author jstroot
 */
public class DetailsViewPresenterImpl implements DetailsView.Presenter {

    @Inject DiskResourceServiceFacade diskResourceService;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject DiskResourceAutoBeanFactory drFactory;
    private final DetailsView view;
    private final Logger LOG = Logger.getLogger(DetailsViewPresenterImpl.class.getSimpleName());

    @Inject
    DetailsViewPresenterImpl(final DetailsViewFactory viewFactory) {
        this.view = viewFactory.create(this);
    }

    @Override
    public DetailsView getView() {
        return view;
    }

    @Override
    public void sendSelectedResourceToEnsembl(DiskResource resource) {

        LOG.fine("Send to ensembl");
    }

    @Override
    public void sendSelectedResourcesToCoge(DiskResource resource) {
        LOG.fine("Send to coge");

    }

    @Override
    public void sendSelectedResourcesToTreeViewer(DiskResource resource) {
        LOG.fine("Send to tree viewer");
    }


}
