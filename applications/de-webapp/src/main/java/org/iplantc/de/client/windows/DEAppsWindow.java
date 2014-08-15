package org.iplantc.de.client.windows;

import org.iplantc.de.apps.client.gin.AppsInjector;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.window.configs.AppsWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.shared.DeModule;

public class DEAppsWindow extends IplantWindowBase {

    private final IplantDisplayStrings displayStrings;
    private final AppsView.Presenter presenter;

    public DEAppsWindow(AppsWindowConfig config) {
        super(null, config);
        displayStrings = org.iplantc.de.resources.client.messages.I18N.DISPLAY;
        presenter = AppsInjector.INSTANCE.getAppsViewPresenter();

        // This must be set before we render view
        ensureDebugId(DeModule.WindowIds.APPS_WINDOW);
        setSize("600", "375");
        setHeadingText(displayStrings.applications());

        presenter.go(this, config.getSelectedAppGroup(), config.getSelectedApp());
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
        config.setSelectedAppGroup(presenter.getSelectedAppGroup());
        return createWindowState(config);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        presenter.setViewDebugId(baseID + AppsModule.Ids.APPS_VIEW);
    }

}
