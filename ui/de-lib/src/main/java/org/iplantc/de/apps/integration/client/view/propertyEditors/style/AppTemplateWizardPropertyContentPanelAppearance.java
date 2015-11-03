package org.iplantc.de.apps.integration.client.view.propertyEditors.style;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.theme.base.client.panel.ContentPanelBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.gray.client.panel.GrayHeaderAppearance;

public class AppTemplateWizardPropertyContentPanelAppearance extends ContentPanelBaseAppearance {

    public interface AppTemplateWizardPropertyContentPanelResources extends ContentPanelResources {
        @Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css", "AppTemplateWizardPropertyContentPanel.css"})
        @Override
        AppTemplateWizardPropertyContentPanelStyle style();
    }

    public interface AppTemplateWizardPropertyContentPanelStyle extends ContentPanelStyle {

    }

    public AppTemplateWizardPropertyContentPanelAppearance() {
        super(GWT.<AppTemplateWizardPropertyContentPanelResources> create(AppTemplateWizardPropertyContentPanelResources.class),
                GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
    }

    public AppTemplateWizardPropertyContentPanelAppearance(AppTemplateWizardPropertyContentPanelResources resources) {
        super(resources, GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
    }

    @Override
    public HeaderDefaultAppearance getHeaderAppearance() {
        return new GrayHeaderAppearance();
    }
}
