package org.iplantc.de.apps.widgets.client.view;

/**
 * @author jstroot
 */
public interface AppLaunchPreviewView extends AppLaunchView {
    @Path("")
    AppTemplateForm getWizard();

    void show();
}
