package org.iplantc.de.diskResource.client;

import org.iplantc.de.diskResource.client.events.ManageCommentsEvent;
import org.iplantc.de.diskResource.client.events.ManageMetadataEvent;
import org.iplantc.de.diskResource.client.events.ManageSharingEvent;
import org.iplantc.de.diskResource.client.events.ShareByDataLinkEvent;
import org.iplantc.de.diskResource.client.events.selection.SendToCogeSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToEnsemblSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToTreeViewerSelected;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;

import com.google.gwt.user.client.ui.IsWidget;

public interface ToolbarView extends IsWidget,
                                     ManageCommentsEvent.HasManageCommentsEventHandlers,
                                     ManageMetadataEvent.HasManageMetadataEventHandlers,
                                     ManageSharingEvent.HasManageSharingEventHandlers,
                                     ShareByDataLinkEvent.HasShareByDataLinkEventHandlers,
                                     SendToEnsemblSelected.HasSendToEnsemblSelectedHandlers,
                                     SendToCogeSelected.HasSendToCogeSelectedHandlers,
                                     SendToTreeViewerSelected.HasSendToTreeViewerSelectedHandlers
{
    interface Appearance {

    }

    interface Presenter {

    }

    DiskResourceSearchField getSearchField();

//    void init(DiskResourceView.Presenter presenter, DiskResourceView view);

    void maskSendToCoGe();

    void maskSendToEnsembl();

    void maskSendToTreeViewer();

    void unmaskSendToCoGe();

    void unmaskSendToEnsembl();

    void unmaskSendToTreeViewer();
}
