package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;

import java.util.List;

/**
 * @author jstroot
 */
public interface DiskResourcePresenterFactory {
    DiskResourceView.Presenter createSelector(@Assisted("hideToolbar") boolean hideToolbar,
                                              @Assisted("hideDetailsPanel") boolean hideDetailsPanel,
                                              @Assisted("singleSelect") boolean singleSelect,
                                              @Assisted("disableFilePreview") boolean disableFilePreview,
                                              HasPath selectedFolder,
                                              IsWidget southWidget);

    DiskResourceView.Presenter withSelectedResources(@Assisted("hideToolbar") boolean hideToolbar,
                                                     @Assisted("hideDetailsPanel") boolean hideDetailsPanel,
                                                     @Assisted("singleSelect") boolean singleSelect,
                                                     @Assisted("disableFilePreview") boolean disableFilePreview,
                                                     HasPath selectedFolder,
                                                     List<HasId> selectedResources);

    DiskResourceView.Presenter createSelectorWithSouthWidgetHeight(@Assisted("hideToolbar") boolean hideToolbar,
                                                                   @Assisted("hideDetailsPanel") boolean hideDetailsPanel,
                                                                   @Assisted("singleSelect") boolean singleSelect,
                                                                   @Assisted("disableFilePreview") boolean disableFilePreview,
                                                                   HasPath selectedFolder,
                                                                   IsWidget southWidget,
                                                                   int southWidgetHeight);
}
