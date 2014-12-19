package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.window.configs.AppsWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

public class DEAppsWindow extends IplantWindowBase {

    private final IplantDisplayStrings displayStrings;
    private final AppsView.Presenter presenter;

    public DEAppsWindow(AppsWindowConfig config,
                        AppsView.Presenter presenter) {
        super(null, config);
        this.presenter = presenter;
        displayStrings = org.iplantc.de.resources.client.messages.I18N.DISPLAY;

        // This must be set before we render view
        ensureDebugId(DeModule.WindowIds.APPS_WINDOW);
        setSize("600", "375");
        setHeadingText(displayStrings.applications());

        presenter.go(this, config.getSelectedAppCategory(), config.getSelectedApp());
    }

    @Override
    public void doHide() {
        presenter.cleanUp();
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
