package org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolPublicAppListWindow;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

/**
 * @author aramsey
 */
public class OverwriteToolDialog extends IPlantDialog implements IsHideable {

    private AppAutoBeanFactory appAutoBeanFactory = GWT.create(AppAutoBeanFactory.class);
    private FlowLayoutContainer container;
    @Inject ToolPublicAppListWindow publicAppListWindow;
    @Inject ToolAdminView.ToolAdminViewAppearance appearance;

    @Inject
    public OverwriteToolDialog(ToolAdminView.ToolAdminViewAppearance appearance,
                               ToolPublicAppListWindow publicAppListWindow) {
        this.appearance = appearance;
        this.publicAppListWindow = publicAppListWindow;
        setResizable(true);
        setMinHeight(200);

        container = addScrollSupport();
        add(container);
    }

    public void setText(Throwable caught) {
        AppList appList =
                AutoBeanCodex.decode(appAutoBeanFactory, AppList.class, caught.getMessage()).as();
        setHeadingText(appearance.confirmOverwriteTitle());

        HTML bodyBeforeApps = new HTML();
        bodyBeforeApps.setHTML(appearance.confirmOverwriteDangerZone());
        HTML bodyAfterApps = new HTML();
        bodyAfterApps.setHTML(appearance.confirmOverwriteBody());

        publicAppListWindow.addApps(appList.getApps());
        container.add(bodyBeforeApps);
        container.add(publicAppListWindow);
        container.add(bodyAfterApps);
    }

    private FlowLayoutContainer addScrollSupport() {
        FlowLayoutContainer container = new FlowLayoutContainer();
        container.getScrollSupport().setScrollMode(ScrollSupport.ScrollMode.AUTO);
        return container;
    }

    @Override
    public void show() {
        super.show();

        ensureDebugId(Belphegor.ToolAdminIds.OVERWRITE_TOOL_DIALOG);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        getOkButton().ensureDebugId(baseID + Belphegor.ToolAdminIds.OKBTN);
        publicAppListWindow.ensureDebugId(baseID + Belphegor.ToolAdminIds.PUBLIC_APPS);
    }
}
