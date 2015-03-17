package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.DiskResourceView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;

import java.util.List;

/**
 * @author jstroot
 */
public interface DiskResourcePresenterFactory {
    DiskResourceView.Presenter filtered(@Assisted("hideToolbar") boolean hideToolbar,
                                        @Assisted("hideDetailsPanel") boolean hideDetailsPanel,
                                        @Assisted("singleSelect") boolean singleSelect,
                                        @Assisted("disableFilePreview") boolean disableFilePreview,
                                        HasPath selectedFolder,
                                        List<InfoType> infoTypeFilters,
                                        TYPE entityType,
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
