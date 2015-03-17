/**
 * 
 */
package org.iplantc.de.apps.integration.client.view.tools;

import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author sriram
 *
 */
public interface DeployedComponentsListingView extends IsWidget {
    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        Tool getSelectedDC();
    }

    public Tool getSelectedDC();

    public void loadDC(List<Tool> list);

    public void mask();

    public void setPresenter(final Presenter presenter);

    public void showInfo(Tool dc);

    public void unmask();

}
