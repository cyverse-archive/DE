package org.iplantc.de.apps.integration.client.view;

import org.iplantc.de.apps.integration.client.view.widgets.ArgumentGroupContentPanelAppearance;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.editors.ArgumentGroupEditorImpl;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.util.AppTemplateUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ArgumentGroupEditorAppEditorImpl extends ArgumentGroupEditorImpl {

    @Inject
    public ArgumentGroupEditorAppEditorImpl(final ArgumentGroupContentPanelAppearance cpAppearance,
                                            final AppTemplateWizardAppearance appearance,
                                            final Provider<AppTemplateForm.ArgumentEditorFactory> argumentEditorProvider,
                                            final AppTemplateUtils appTemplateUtils) {
        super(cpAppearance, appearance, argumentEditorProvider, appTemplateUtils);
    }

}
