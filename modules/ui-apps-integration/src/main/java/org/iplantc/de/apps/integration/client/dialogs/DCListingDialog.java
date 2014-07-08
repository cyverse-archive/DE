/**
 * 
 */
package org.iplantc.de.apps.integration.client.dialogs;

import org.iplantc.de.apps.integration.client.presenter.DeployedComponentPresenterImpl;
import org.iplantc.de.apps.integration.client.view.deployedComponents.DeployedComponentsListingView;
import org.iplantc.de.apps.integration.client.view.deployedComponents.DeployedComponentsListingViewImpl;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
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
                getButton(PredefinedButton.OK).setEnabled(true);
                selectedComponent = items.get(0);
            } else {
                getButton(PredefinedButton.OK).setEnabled(false);
                selectedComponent = null;
            }
        }
    }

    private DeployedComponent selectedComponent = null;

    public DCListingDialog() {
        setPixelSize(600, 500);
        setResizable(false);
        setModal(true);
        setHeadingText("Installed Tools");
        setHideOnButtonClick(false);
        getOkButton().setEnabled(false);
        getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                selectedComponent = null;
                hide();
            }
        });
        getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });

        ListStore<DeployedComponent> listStore = new ListStore<DeployedComponent>(new DCKeyProvider());
        DeployedComponentsListingView view = new DeployedComponentsListingViewImpl(listStore, new DCSelectionChangedHandler());
        DeployedComponentsListingView.Presenter p = new DeployedComponentPresenterImpl(view);
        p.go(this);

    }

    public DeployedComponent getSelectedComponent() {
        return selectedComponent;
    }

}
