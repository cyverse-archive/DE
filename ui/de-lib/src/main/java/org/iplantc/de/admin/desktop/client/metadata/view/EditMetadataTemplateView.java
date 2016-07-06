package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView.Presenter;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

public interface EditMetadataTemplateView extends IsWidget, IsMaskable {

    public interface EditMetadataTemplateViewAppearance {

        ImageResource addIcon();

        ImageResource deleteIcon();

        String valColumn();

        String defColumn();

        String addBtn();

        String delBtn();

        String enumError();

        int tempNameMaxLength();
    }

    void setPresenter(Presenter p);

    MetadataTemplate getTemplate();

    boolean validate();

    void edit(MetadataTemplate result);

    void reset();

}
