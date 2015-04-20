package org.iplantc.de.theme.base.client.admin.metadata;

import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView.Presenter.MetadataPresenterAppearance;

import com.google.gwt.core.client.GWT;

public class DefaultMetadataPresenterAppearance implements MetadataPresenterAppearance {

    private final MetadataDisplayStrings displayStrings;

    public DefaultMetadataPresenterAppearance() {
        this(GWT.<MetadataDisplayStrings> create(MetadataDisplayStrings.class));
    }

    public DefaultMetadataPresenterAppearance(MetadataDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;
    }

    @Override
    public String templateRetrieveError() {
        return displayStrings.templateRetrieveError();
    }

    @Override
    public String deleteTemplateConfirm() {
        return displayStrings.deleteTemplateConfirm();
    }

    @Override
    public String deleteTemplateError() {
        return displayStrings.deleteTemplateError();
    }

    @Override
    public String deleteTemplateSuccess() {
        return displayStrings.deleteTemplateSuccess();
    }

    @Override
    public String enumError() {
        return displayStrings.enumError();
    }

    @Override
    public String addTemplateError() {
        return displayStrings.addTemplateError();
    }

    @Override
    public String addTemplateSuccess() {
        return displayStrings.addTemplateSuccess();
    }

    @Override
    public String updateTemplateSuccess() {
        return displayStrings.updateTemplateSuccess();
    }

    @Override
    public String updateTemplateError() {
        return displayStrings.updateTemplateError();
    }

    @Override
    public String templateAttributeEditorHeading() {
        return displayStrings.templateAttributeEditorHeading();
    }

}
