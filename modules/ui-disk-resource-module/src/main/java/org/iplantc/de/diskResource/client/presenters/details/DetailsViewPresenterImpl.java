package org.iplantc.de.diskResource.client.presenters.details;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.events.RequestSendToCoGeEvent;
import org.iplantc.de.diskResource.client.events.RequestSendToEnsemblEvent;
import org.iplantc.de.diskResource.client.events.RequestSendToTreeViewerEvent;
import org.iplantc.de.diskResource.client.gin.factory.DetailsViewFactory;

import com.google.inject.Inject;

import java.util.logging.Logger;

/**
 * @author jstroot
 */
public class DetailsViewPresenterImpl implements DetailsView.Presenter {

    @Inject DiskResourceUtil diskResourceUtil;
    @Inject EventBus eventBus;
    @Inject IplantAnnouncer announcer;
    @Inject DetailsView.Presenter.Appearance appearance;

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
        InfoType infoType = InfoType.fromTypeString(resource.getInfoType());
        if (infoType == null
                || !diskResourceUtil.isEnsemblInfoType(infoType)) {

            announcer.schedule(new ErrorAnnouncementConfig(appearance.unsupportedEnsemblInfoType()));
            return;
        }
        eventBus.fireEvent(new RequestSendToEnsemblEvent((File)resource, infoType));
    }

    @Override
    public void sendSelectedResourcesToCoge(DiskResource resource) {
        InfoType infoType = InfoType.fromTypeString(resource.getInfoType());
        if (infoType == null
                || !diskResourceUtil.isGenomeVizInfoType(infoType)) {

            announcer.schedule(new ErrorAnnouncementConfig(appearance.unsupportedCogeInfoType()));
            return;
        }
        eventBus.fireEvent(new RequestSendToCoGeEvent((File) resource));
    }

    @Override
    public void sendSelectedResourcesToTreeViewer(DiskResource resource) {
        InfoType infoType = InfoType.fromTypeString(resource.getInfoType());
        if (infoType == null
                || !diskResourceUtil.isTreeInfoType(infoType)) {

            announcer.schedule(new ErrorAnnouncementConfig(appearance.unsupportedTreeInfoType()));
            return;
        }
        eventBus.fireEvent(new RequestSendToTreeViewerEvent((File) resource));
    }


}
