/**
 * 
 */
package org.iplantc.de.apps.widgets.client.view.deployedComponents;

import org.iplantc.de.client.models.deployedComps.DeployedComponent;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author sriram
 *
 */
public interface DeployedComponentsListingView extends IsWidget {
    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        DeployedComponent getSelectedDC();
    }

    public DeployedComponent getSelectedDC();

    public void loadDC(List<DeployedComponent> list);

    public void mask();

    public void setPresenter(final Presenter presenter);

    public void showInfo(DeployedComponent dc);

    public void unmask();

}
