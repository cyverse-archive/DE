package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.admin.desktop.client.metadata.events.AddMetadataSelectedEvent;
import org.iplantc.de.admin.desktop.client.metadata.events.DeleteMetadataSelectedEvent;
import org.iplantc.de.admin.desktop.client.metadata.events.EditMetadataSelectedEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface TemplateListingView extends IsWidget, IsMaskable,
                                             EditMetadataSelectedEvent.HasEditMetadataSelectedEventHandlers,
                                             AddMetadataSelectedEvent.HasAddMetadataSelectedEventHandlers,
                                             DeleteMetadataSelectedEvent.HasDeleteMetadataSelectedEventHandlers {

    public interface TemplateListingAppearance {
        String add();

        String delete();

        String deleted();

        String edit();

        ImageResource addIcon();

        ImageResource deleteIcon();

        ImageResource editIcon();

        String createdByColumn();

        String createdOnColumn();

        String createdBy();

        String createdOn();

        String lastModified();

        String lastModBy();

        String nameColumn();

        String descriptionColumn();
    }

    public interface Presenter {
        public interface MetadataPresenterAppearance {

            String templateRetrieveError();
            
            String deleteTemplateConfirm();
            
            String deleteTemplateError();
            
            String deleteTemplateSuccess();
            
            String enumError();

            String addTemplateError();

            String addTemplateSuccess();

            String updateTemplateSuccess();

            String updateTemplateError();

            String templateAttributeEditorHeading();

        }

        void go(HasOneWidget container);

        void setViewDebugId(String baseId);
    }

    void loadTemplates(List<MetadataTemplateInfo> result);

    void remove(MetadataTemplateInfo template);

}
