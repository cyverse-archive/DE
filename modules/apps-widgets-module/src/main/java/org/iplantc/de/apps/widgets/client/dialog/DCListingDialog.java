/**
 * 
 */
package org.iplantc.de.apps.widgets.client.dialog;

import org.iplantc.de.apps.widgets.client.presenter.DeployedComponentPresenterImpl;
import org.iplantc.de.apps.widgets.client.view.deployedComponents.DeployedComponentsListingView;
import org.iplantc.de.apps.widgets.client.view.deployedComponents.DeployedComponentsListingViewImpl;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.List;

/**
 * 
 * @author sriram
 * 
 */
public class DCListingDialog extends IPlantDialog {

    class DCKeyProvider implements ModelKeyProvider<DeployedComponent> {

        @Override
        public String getKey(DeployedComponent item) {
            return item.getId();
        }

    }
    
    class DCSelectionChangedHandler implements SelectionChangedHandler<DeployedComponent> {

        @Override
        public void onSelectionChanged(SelectionChangedEvent<DeployedComponent> event) {
            List<DeployedComponent> items = event.getSelection();
            if (items != null && items.size() > 0) {
                getButtonById(PredefinedButton.OK.toString()).setEnabled(true);
                selectedComponent = items.get(0);
            } else {
                getButtonById(PredefinedButton.OK.toString()).setEnabled(false);
                selectedComponent = null;
            }

        }

    }

    private DeployedComponent selectedComponent = null;


    public DCListingDialog() {
        setHideOnButtonClick(true);
        setPixelSize(600, 500);
        setResizable(false);
        setModal(true);
        setHeadingText("Installed Tools");
        getOkButton().setEnabled(false);

        ListStore<DeployedComponent> listStore = new ListStore<DeployedComponent>(new DCKeyProvider());
        DeployedComponentsListingView view = new DeployedComponentsListingViewImpl(listStore,
                new DCSelectionChangedHandler());
        DeployedComponentsListingView.Presenter p = new DeployedComponentPresenterImpl(view);
        p.go(this);

    }

    public DeployedComponent getSelectedComponent() {
        return selectedComponent;
    }

}
