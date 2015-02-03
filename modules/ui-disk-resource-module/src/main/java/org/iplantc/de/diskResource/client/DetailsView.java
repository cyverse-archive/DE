package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
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
        interface DetailsViewStyle extends CssResource {

            String disabledHyperlink();

            String label();

            String value();

            String hidden();

            String hyperlink();

            String deselectIcon();
        }

        String coge();

        String createdDate();

        String delete();

        String ensembl();

        String files();

        String folders();

        String infoTypeDisabled();

        String lastModified();

        String noDetails();

        String beginSharing();

        String permissions();

        String selectInfoType();

        String sendTo();

        String share();

        String sharingDisabled();

        String size();

        String treeViewer();

        DetailsViewStyle css();

        String viewersDisabled();

        ImageResource deselectInfoTypeIcon();
    }

    interface Presenter {

        DetailsView getView();

        void sendSelectedResourceToEnsembl(DiskResource resource);

        void sendSelectedResourcesToCoge(DiskResource resource);

        void sendSelectedResourcesToTreeViewer(DiskResource resource);
    }
}
