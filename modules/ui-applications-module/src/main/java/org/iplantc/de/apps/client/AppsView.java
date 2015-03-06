package org.iplantc.de.apps.client;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * @author jstroot
 */
public interface AppsView extends IsWidget,
                                  IsMaskable {

    interface AppsViewAppearance {

    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {


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

    AppCategory getAppCategoryFromElement(Element el);

    App getAppFromElement(Element el);

}
