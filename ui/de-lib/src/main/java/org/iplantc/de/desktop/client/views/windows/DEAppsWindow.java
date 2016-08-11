package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.dialogs.AgaveAuthPrompt;
import org.iplantc.de.commons.client.views.window.configs.AppsWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.inject.Inject;

/**
 * @author jstroot
 */
public class DEAppsWindow extends IplantWindowBase {

    @Inject UserInfo userInfo;
    private final AppsView.Presenter presenter;

    @Inject
    DEAppsWindow(final AppsView.Presenter presenter, final IplantDisplayStrings displayStrings) {
        this.presenter = presenter;

        // This must be set before we render view
        ensureDebugId(DeModule.WindowIds.APPS_WINDOW);
        setSize("600", "375");
        setHeadingText(displayStrings.applications());
    }

    @Override
    public <C extends WindowConfig> void show(final C windowConfig,
                                              final String tag,
                                              final boolean isMaximizable) {
        final AppsWindowConfig appsWindowConfig = (AppsWindowConfig)windowConfig;
        presenter.go(this, appsWindowConfig.getSelectedAppCategory(), appsWindowConfig.getSelectedApp());
        super.show(windowConfig, tag, isMaximizable);
    }

    @Override
    protected void afterShow() {
        super.afterShow();
        if (userInfo.hasAgaveRedirect()) {
            AgaveAuthPrompt prompt = AgaveAuthPrompt.getInstance();
            prompt.show();
        }
    }

    @Override
    public void doHide() {
        super.doHide();
    }

    @Override
    public WindowState getWindowState() {
        AppsWindowConfig config = ConfigFactory.appsWindowConfig();
        config.setSelectedApp(presenter.getSelectedApp());
        config.setSelectedAppCategory(presenter.getSelectedAppCategory());
        return createWindowState(config);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        presenter.setViewDebugId(baseID + AppsModule.Ids.APPS_VIEW);
    }

}
