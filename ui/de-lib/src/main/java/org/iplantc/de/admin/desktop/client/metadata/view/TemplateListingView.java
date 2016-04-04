package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface TemplateListingView extends IsWidget, IsMaskable {

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

        void deleteTemplate(MetadataTemplateInfo template);

        void editTemplate(MetadataTemplateInfo template);

        void addTemplate();

        void setViewDebugId(String baseId);
    }

    void loadTemplates(List<MetadataTemplateInfo> result);

    void setPresenter(Presenter p);

    void remove(MetadataTemplateInfo template);

}
