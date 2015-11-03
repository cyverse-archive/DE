package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.selection.AppDetailsDocSelected;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.SaveMarkdownSelected;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by jstroot on 3/2/15.
 * @author jstroot
 */
public interface AppDetailsView extends IsWidget,
                                        Editor<App>,
                                        AppUpdatedEvent.AppUpdatedEventHandler,
                                        AppFavoriteSelectedEvent.HasAppFavoriteSelectedEventHandlers,
                                        AppDetailsDocSelected.HasAppDetailsDocSelectedHandlers,
                                        SaveMarkdownSelected.HasSaveMarkdownSelectedHandlers,
                                        AppRatingDeselected.HasAppRatingDeselectedHandlers,
                                        AppRatingSelected.HasAppRatingSelectedEventHandlers {

    interface AppDetailsAppearance {
        interface AppDetailsStyle extends CssResource {

            String label();

            String value();

            String hyperlink();

            String detailsTable();

            String detailsRow();

            String tabPanel();
        }

        String descriptionLabel();

        AppDetailsStyle css();

        String detailsLabel();

        SafeHtml getAppDocError(Throwable caught);

        SafeHtml getCategoriesHtml(List<List<String>> appGroupHierarchies);

        SafeHtml highlightText(String value, String searchRegexPattern);

        String publishedOnLabel();

        String integratorNameLabel();

        String integratorEmailLabel();

        String helpLabel();

        String ratingLabel();

        String categoriesLabel();

        String informationTabLabel();

        SafeHtml saveAppDocError(Throwable caught);

        String toolInformationTabLabel();

        String toolNameLabel();

        String toolPathLabel();

        String toolVersionLabel();

        String toolAttributionLabel();

        String userManual();

        String url();

        String appUrl();

        String copyAppUrl();
    }

    interface Presenter extends AppFavoriteSelectedEvent.HasAppFavoriteSelectedEventHandlers,
                                AppRatingDeselected.HasAppRatingDeselectedHandlers,
                                AppRatingSelected.HasAppRatingSelectedEventHandlers {

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
