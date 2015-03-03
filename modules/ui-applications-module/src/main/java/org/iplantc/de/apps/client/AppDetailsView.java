package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by jstroot on 3/2/15.
 * @author jstroot
 */
public interface AppDetailsView extends IsWidget,
                                        AppFavoritedEvent.AppFavoritedEventHandler,
                                        AppFavoriteSelectedEvent.HasAppFavoriteSelectedEventHandlers {

    interface Presenter {

        AppDetailsView getView();

        void go(HasOneWidget widget, App app, String searchRegexPattern,
                List<List<String>> appGroupHierarchies);
    }

}
