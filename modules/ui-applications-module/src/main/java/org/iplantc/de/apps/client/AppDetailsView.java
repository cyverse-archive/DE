package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.selection.AppDetailsDocSelected;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.SaveMarkdownSelected;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by jstroot on 3/2/15.
 * @author jstroot
 */
public interface AppDetailsView extends IsWidget,
                                        Editor<App>,
                                        AppFavoritedEvent.AppFavoritedEventHandler,
                                        AppFavoriteSelectedEvent.HasAppFavoriteSelectedEventHandlers,
                                        AppDetailsDocSelected.HasAppDetailsDocSelectedHandlers,
                                        SaveMarkdownSelected.HasSaveMarkdownSelectedHandlers {

    interface AppDetailsAppearance {
        interface AppDetailsStyle extends CssResource {

            String label();

            String value();

            String hyperlink();
        }

        String descriptionLabel();

        AppDetailsStyle css();

        String detailsLabel();

        String publishedOnLabel();

        String integratorNameLabel();

        String integratorEmailLabel();

        String helpLabel();

        String ratingLabel();

        String categoriesLabel();

        String informationTabLabel();

        String toolInformationTabLabel();

        String toolNameLabel();

        String toolPathLabel();

        String toolVersionLabel();

        String toolAttributionLabel();
    }

    interface Presenter {

        void go(HasOneWidget widget,
                App app,
                String searchRegexPattern,
                List<List<String>> appGroupHierarchies);
    }

    /**
     * Displays the documentation window
     */
    void showDoc(AppDoc appDoc);
}
