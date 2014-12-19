package org.iplantc.de.theme.base.client.desktop.window;

import org.iplantc.de.desktop.client.views.windows.AppEditorWindow;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

public class AppEditorWindowBaseAppearance implements AppEditorWindow.AppEditorAppearance {

    private final AppsWidgetsPropertyPanelLabels appWidgetsLabels = GWT.create(AppsWidgetsPropertyPanelLabels.class);
    private final IplantDisplayStrings displayStrings = GWT.create(IplantDisplayStrings.class);

    interface PublicAppTitleTemplate extends SafeHtmlTemplates {
        @Template("<div>"
                      + "<span class='{3}'>{2}</span>"
                      + "<span class='{1}'>{0}</span>"
                      + "</div>")
        SafeHtml editPublicAppWarningTitle(SafeHtml title, String titleStyle, String warningText, String warningStyle);
    }
    private final PublicAppTitleTemplate templates;

    interface TitleStyles extends CssResource {
        String warning();

        String title();
    }

    interface AppEditorAppearanceResources extends ClientBundle {
        @Source("org/iplantc/de/theme/base/client/desktop/window/AppIntegrationWindowTitleStyles.css")
        TitleStyles titleStyles();
    }

    private final AppEditorAppearanceResources resources;

    public AppEditorWindowBaseAppearance(){
        this(GWT.<AppEditorAppearanceResources> create(AppEditorAppearanceResources.class),
             GWT.<PublicAppTitleTemplate> create(PublicAppTitleTemplate.class));
    }

    public AppEditorWindowBaseAppearance(final AppEditorAppearanceResources resources,
                                         final PublicAppTitleTemplate templates){

        this.resources = resources;
        this.templates = templates;
        this.resources.titleStyles().ensureInjected();
    }

    @Override
    public String appDefaultName() {
        return "New app";
    }

    @Override
    public String appPublishedError() {
        return "This app has been published before the current changes were saved. All unsaved changes have been discarded.";
    }

    @Override
    public SafeHtml editPublicAppWarningTitle(SafeHtml appName) {
        return templates.editPublicAppWarningTitle(appName,
                                                   resources.titleStyles().title(),
                                                   "You are now editing your public app.",
                                                   resources.titleStyles().warning());
    }

    @Override
    public String groupDefaultLabel(int i) {
        return appWidgetsLabels.groupDefaultLabel(i);
    }

    @Override
    public String headingText() {
        return "Create App";
    }

    @Override
    public String loadingMask() {
        return displayStrings.loadingMask();
    }

    @Override
    public int minHeight() {
        return 375;
    }

    @Override
    public int minWidth() {
        return 725;
    }

    @Override
    public String unableToRetrieveWorkflowGuide() {
        return "Unable to open the selected App";
    }
}
