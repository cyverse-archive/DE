package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface TemplateListingView extends IsWidget, IsMaskable {

    public interface TemplateListingAppearance {

    }

    public interface Presenter {
        void go(HasOneWidget container);

        void deleteTemplate(MetadataTemplateInfo template);

        void editTemplate(MetadataTemplateInfo template);

        void addTemplate();
    }

    void loadTemplates(List<MetadataTemplateInfo> result);

    void setPresenter(Presenter p);

    void remove(MetadataTemplateInfo template);

}
