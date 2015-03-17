/**
 *
 */
package org.iplantc.de.apps.integration.client.presenter;

import org.iplantc.de.apps.integration.client.view.tools.DeployedComponentsListingView;
import org.iplantc.de.apps.integration.shared.AppIntegrationModule;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.user.client.ui.HasOneWidget;

/**
 * @author sriram
 *
 */
public class DeployedComponentPresenterImpl implements DeployedComponentsListingView.Presenter {

    private final DeployedComponentsListingView view;

    public DeployedComponentPresenterImpl(DeployedComponentsListingView view) {
        this.view = view;
        this.view.setPresenter(this);
    }


    /* (non-Javadoc)
     * @see org.iplantc.core.appsIntegration.client.view.DeployedComponentsListingView.Presenter#getSelectedDC()
     */
    @Override
    public Tool getSelectedDC() {
        return view.getSelectedDC();
    }

    @Override
    public void go(HasOneWidget container) {

        view.asWidget().ensureDebugId(AppIntegrationModule.Ids.INSTALLED_TOOLS_DLG);
        container.setWidget(view.asWidget());
    }
}
