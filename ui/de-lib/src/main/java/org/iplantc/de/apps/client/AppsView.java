package org.iplantc.de.apps.client;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * This is the primary view for the Apps module. It is a composite of all other views within the
 * module.
 *
 *
 * @author jstroot
 */
public interface AppsView extends IsWidget,
                                  IsMaskable {

    interface AppsViewAppearance {

    }

    /**
     * This presenter is responsible for wiring all of the events from the other MVP modules
     * together.
     */
    interface Presenter {

        App getSelectedApp();

        AppCategory getSelectedAppCategory();

        void go(HasOneWidget container, HasId selectedAppCategory, HasId selectedApp);

        Grid<App> getAppsGrid();

        Presenter hideAppMenu();

        Presenter hideWorkflowMenu();

        void setViewDebugId(String baseId);
    }

    void hideAppMenu();

    void hideWorkflowMenu();

}
