package org.iplantc.de.diskResource;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.event.StoreUpdateEvent;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public interface DetailsView extends IsWidget,
                                     DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler,
                                     StoreUpdateEvent.StoreUpdateHandler<DiskResource>,
                                     ManageSharingSelected.HasManageSharingSelectedEventHandlers,
                                     EditInfoTypeSelected.HasEditInfoTypeSelectedEventHandlers {
    interface Appearance {

        String coge();

        String createdDate();

        String delete();

        String ensembl();

        String files();

        String folders();

        String lastModified();

        String noDetails();

        String nosharing();

        String permissions();

        String sendTo();

        String share();

        String size();

        String treeViewer();
    }

    interface Presenter {

        DetailsView getView();

        void sendSelectedResourceToEnsembl(DiskResource resource);

        void sendSelectedResourcesToCoge(DiskResource resource);

        void sendSelectedResourcesToTreeViewer(DiskResource resource);
    }
}
