package org.iplantc.de.apps.client.views.details;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.selection.AppDetailsDocSelected;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.SaveMarkdownSelected;
import org.iplantc.de.apps.client.views.details.doc.AppDocMarkdownDialog;
import org.iplantc.de.apps.client.views.grid.cells.AppFavoriteCellWidget;
import org.iplantc.de.apps.client.views.grid.cells.AppRatingCellWidget;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;

import java.util.List;

/**
 * @author jstroot
 */
public class AppDetailsViewImpl extends Composite implements AppDetailsView,
                                                             SaveMarkdownSelected.SaveMarkdownSelectedHandler {


    @UiTemplate("AppDetailsViewImpl.ui.xml")
    interface AppInfoViewUiBinder extends UiBinder<TabPanel, AppDetailsViewImpl> { }

    /**
     * Editor source class for binding to App.getTools()
     */
    private class ToolEditorSource extends EditorSource<ToolDetailsView> {
        private final AccordionLayoutContainer toolsContainer;

        public ToolEditorSource(final AccordionLayoutContainer toolsContainer) {
            this.toolsContainer = toolsContainer;
        }

        @Override
        public ToolDetailsView create(int index) {
            final ToolDetailsView toolDetailsView = new ToolDetailsView();
            toolsContainer.insert(toolDetailsView, index);
            return toolDetailsView;
        }

        @Override
        public void dispose(ToolDetailsView subEditor) {
            subEditor.removeFromParent();
        }
    }

    interface AppDetailsEditorDriver extends SimpleBeanEditorDriver<App, AppDetailsViewImpl> {}

    private final AppInfoViewUiBinder BINDER = GWT.create(AppInfoViewUiBinder.class);
    private final AppDetailsEditorDriver editorDriver = GWT.create(AppDetailsEditorDriver.class);

    @UiField @Path("") AppFavoriteCellWidget favIcon; // Bind to app

    @UiField(provided = true) AppDetailsAppearance appearance;
    /**
     * FIXME Ensure highlighting
     */
    @UiField InlineLabel integratorName;
    /**
     * FIXME Ensure highlighting
     */
    @UiField InlineLabel integratorEmail;
    /**
     * FIXME Not bound directly. Value given at init/construction time
     */
    @UiField @Ignore InlineLabel categories;
    @UiField @Path("") AppRatingCellWidget ratings; // Bind to app
    @UiField @Ignore InlineHyperlink helpLink;
    @UiField AccordionLayoutContainer toolsContainer;
    /**
     * FIXME Ensure highlighting
     */
    @UiField InlineHTML description;

    final ListEditor<Tool, ToolDetailsView> tools;
    private final App app;

    @Inject UserInfo userInfo;

    @Inject
    AppDetailsViewImpl(final AppDetailsView.AppDetailsAppearance appearance,
                       @Assisted final App app,
                       @Assisted final String searchRegexPattern,
                       @Assisted final List<List<String>> appGroupHierarchies) {
        this.appearance = appearance;
        this.app = app;
        this.tools = ListEditor.of(new ToolEditorSource(toolsContainer));

        // TODO Use appearance to write group hierarchies
        /* TODO Create a LeafValueEditor which highlights text as it is bound
         *        This will apply to name, email, description
         */
        initWidget(BINDER.createAndBindUi(this));
        editorDriver.initialize(this);
    }

    @Override
    public HandlerRegistration addAppDetailsDocSelectedHandler(AppDetailsDocSelected.AppDetailsDocSelectedHandler handler) {
        return addHandler(handler, AppDetailsDocSelected.TYPE);
    }

    @Override
    public HandlerRegistration addAppFavoriteSelectedEventHandlers(AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler handler) {
        return favIcon.addAppFavoriteSelectedEventHandlers(handler);
    }

    @Override
    public HandlerRegistration addSaveMarkdownSelectedHandler(SaveMarkdownSelected.SaveMarkdownSelectedHandler handler) {
        return addHandler(handler, SaveMarkdownSelected.TYPE);
    }

    @Override
    public void onAppFavorited(AppFavoritedEvent appFavoritedEvent) {
        favIcon.setValue(appFavoritedEvent.getApp());
    }

    @Override
    public void onSaveMarkdownSelected(SaveMarkdownSelected event) {
        // Forward event
        fireEvent(event);
    }

    @Override
    public void showDoc(AppDoc appDoc) {
        AppDocMarkdownDialog markdownDialog = new AppDocMarkdownDialog(app, appDoc, userInfo);
        markdownDialog.show();
        markdownDialog.addSaveMarkdownSelectedHandler(this);
    }

    @UiHandler("helpLink")
    void onHelpSelected(ClickEvent event) {
        fireEvent(new AppDetailsDocSelected(app));
    }

    public static native String render(String val) /*-{
		var markdown = $wnd.Markdown.getSanitizingConverter();
		return markdown.makeHtml(val);
    }-*/;

}
