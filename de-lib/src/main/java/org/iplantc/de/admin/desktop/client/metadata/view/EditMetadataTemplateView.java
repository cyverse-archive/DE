package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView.Presenter;
import org.iplantc.de.client.models.IsMaskable;

import com.google.gwt.user.client.ui.IsWidget;

public interface EditMetadataTemplateView extends IsWidget, IsMaskable {

    void setPresenter(Presenter p);

}
