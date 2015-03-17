/**
 *
 */
package org.iplantc.de.apps.integration.client.dialogs;

import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids.*;
import org.iplantc.de.apps.integration.client.gin.factory.DeployedComponentListingViewFactory;
import org.iplantc.de.apps.integration.client.presenter.DeployedComponentPresenterImpl;
import org.iplantc.de.apps.integration.client.view.tools.DeployedComponentsListingView;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.inject.Inject;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.List;

/**
 * @author sriram
 */
public class DCListingDialog extends IPlantDialog {

    class DCKeyProvider implements ModelKeyProvider<Tool> {

        @Override
        public String getKey(Tool item) {
            return item.getId();
        }

    }

    class DCSelectionChangedHandler implements SelectionChangedHandler<Tool> {

        @Override
        public void onSelectionChanged(SelectionChangedEvent<Tool> event) {
            List<Tool> items = event.getSelection();
            if (items != null && items.size() > 0) {
                getButton(PredefinedButton.OK).setEnabled(true);
                selectedComponent = items.get(0);
            } else {
                getButton(PredefinedButton.OK).setEnabled(false);
                selectedComponent = null;
            }
        }
    }

    private Tool selectedComponent = null;

    @Inject
    DCListingDialog(final DeployedComponentListingViewFactory viewFactory) {
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

        ListStore<Tool> listStore = new ListStore<>(new DCKeyProvider());
        DeployedComponentsListingView view = viewFactory.createDcListingView(listStore, new DCSelectionChangedHandler());
        DeployedComponentsListingView.Presenter p = new DeployedComponentPresenterImpl(view);
        getButton(PredefinedButton.OK).ensureDebugId(INSTALLED_TOOLS_DLG + OK);
        getButton(PredefinedButton.CANCEL).ensureDebugId(INSTALLED_TOOLS_DLG + CANCEL);
        p.go(this);

    }

    public Tool getSelectedComponent() {
        return selectedComponent;
    }

}
