package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.HasSubmitDiskResourceQueryEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected.HasEditInfoTypeSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected.HasManageSharingSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.Md5ValueClicked.HasMd5ValueClickedHandlers;
import org.iplantc.de.diskResource.client.events.selection.Md5ValueClicked.Md5ValueClickedHandler;
import org.iplantc.de.diskResource.client.events.selection.ResetInfoTypeSelected.HasResetInfoTypeSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.SendToCogeSelected.HasSendToCogeSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.SendToEnsemblSelected.HasSendToEnsemblSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.SendToTreeViewerSelected.HasSendToTreeViewerSelectedHandlers;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.event.StoreUpdateEvent.StoreUpdateHandler;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public interface DetailsView extends IsWidget,
                                     DiskResourceSelectionChangedEventHandler,
                                     StoreUpdateHandler<DiskResource>,
                                     HasManageSharingSelectedEventHandlers,
                                     HasEditInfoTypeSelectedEventHandlers,
                                     HasResetInfoTypeSelectedHandlers,
                                     HasSubmitDiskResourceQueryEventHandlers,
                                     HasSendToTreeViewerSelectedHandlers,
                                     HasSendToCogeSelectedHandlers,
                            HasSendToEnsemblSelectedHandlers,
                            HasMd5ValueClickedHandlers {
    interface Appearance {
        interface DetailsViewStyle extends CssResource {

            String disabledHyperlink();

            String label();

            String value();

            String hidden();

            String hyperlink();

            String deselectIcon();

            String table();

            String tagSearch();

            String row();

            String emptyDetails();
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

        String tagsLabel();

        String filesFoldersLabel();

        String sendToLabel();

        String infoTypeLabel();

        String md5CheckSum();

        String typeLabel();

        String sizeLabel();

        String shareLabel();

        String permissionsLabel();

        String createdDateLabel();

        String lastModifiedLabel();

    }

    interface Presenter {

        interface Appearance {

            String tagAttachError();

            String tagAttached(String name, String value);

            String tagDetachError();

            String tagDetached(String value, String name);
        }

        void attachTagToResource(Tag tag, DiskResource resource);

        DetailsView getView();

        void removeTagFromResource(Tag tag, DiskResource resource);

    }
}
