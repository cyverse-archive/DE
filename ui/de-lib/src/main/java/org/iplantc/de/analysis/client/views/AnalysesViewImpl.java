package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.AnalysisToolBarView;
import org.iplantc.de.analysis.client.events.HTAnalysisExpandEvent.HTAnalysisExpandEventHandler;
import org.iplantc.de.analysis.client.events.selection.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisCommentSelectedEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.gin.factory.AnalysisToolBarFactory;
import org.iplantc.de.analysis.client.models.AnalysisFilter;
import org.iplantc.de.analysis.client.views.dialogs.AnalysisCommentsDialog;
import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * @author sriram, jstroot
 */
public class AnalysesViewImpl extends Composite implements AnalysesView,
                                                           AnalysisCommentSelectedEvent.AnalysisCommentSelectedEventHandler,
                                                           SelectionChangedHandler<Analysis> {

    @UiTemplate("AnalysesViewImpl.ui.xml")
    interface MyUiBinder extends UiBinder<BorderLayoutContainer, AnalysesViewImpl> {
    }

    @UiField(provided = true) final ColumnModel<Analysis> cm;
    @UiField(provided = true) final ListStore<Analysis> listStore;
    @UiField(provided = true) final AnalysisToolBarView toolBar;
    @UiField Appearance appearance;
    @UiField Grid<Analysis> grid;
    @UiField LiveGridView<Analysis> gridView;
    @UiField ToolBar pagingToolBar;
    @UiField Status selectionStatus;
    private final AnalysesView.Presenter presenter;

    @Inject
    AnalysesViewImpl(final AnalysisColumnModel cm,
                     final AnalysisToolBarFactory toolBarFactory,
                     @Assisted final ListStore<Analysis> listStore,
                     @Assisted final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader,
                     @Assisted AnalysesView.Presenter presenter) {
        this.listStore = listStore;
        this.cm = cm;
        this.presenter = presenter;
        this.toolBar = toolBarFactory.create(presenter, loader);

        MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
        initWidget(uiBinder.createAndBindUi(this));

        pagingToolBar.addStyleName(appearance.pagingToolbarStyle());
        pagingToolBar.setBorders(false);

        // Init Grid
        grid.setLoader(loader);
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.MULTI);
        grid.setLoadMask(true);
        grid.setSelectionModel(new CheckBoxSelectionModel<>(new IdentityValueProvider<Analysis>()));

        // Init Toolbar
        pagingToolBar.insert(new LiveToolItem(grid), 0);
        setSelectionCount(0);

        // Wire up eventHandlers
        grid.getSelectionModel().addSelectionChangedHandler(this);
        grid.getSelectionModel().addSelectionChangedHandler(toolBar);
        cm.addAnalysisCommentSelectedEventHandler(this);

    }

    //<editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration addAnalysisAppSelectedEventHandler(AnalysisAppSelectedEvent.AnalysisAppSelectedEventHandler handler) {
        return ((AnalysisColumnModel) cm).addAnalysisAppSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addAnalysisNameSelectedEventHandler(AnalysisNameSelectedEvent.AnalysisNameSelectedEventHandler handler) {
        return ((AnalysisColumnModel) cm).addAnalysisNameSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addHTAnalysisExpandEventHandler(HTAnalysisExpandEventHandler handler) {
        return ((AnalysisColumnModel) cm).addHTAnalysisExpandEventHandler(handler);
    }
    //</editor-fold>

    @Override
    public void filterByAnalysisId(String analysisId, String name) {
        toolBar.filterByAnalysisId(analysisId, name);
    }

    @Override
    public void filterByParentAnalysisId(String id) {
        toolBar.filterByParentAnalysisId(id);
    }

    @Override
    public List<Analysis> getSelectedAnalyses() {
        return grid.getSelectionModel().getSelectedItems();
    }

    @Override
    public void onAnalysisCommentSelected(final AnalysisCommentSelectedEvent event) {
        // Show comments
        final AnalysisCommentsDialog d = new AnalysisCommentsDialog(event.getValue());
        d.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent hideEvent) {
                if (Dialog.PredefinedButton.OK.equals(hideEvent.getHideButton())
                        && d.isCommentChanged()) {
                    presenter.updateAnalysisComment(event.getValue(),
                                                    d.getComment());

                }
            }
        });
        d.show();
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {
        setSelectionCount(event.getSelection().size());
    }

    @Override
    public void setSelectedAnalyses(List<Analysis> selectedAnalyses) {
        if (selectedAnalyses != null) {
            grid.getSelectionModel().setSelection(selectedAnalyses);

            if (!selectedAnalyses.isEmpty()) {
                grid.getView().ensureVisible(listStore.indexOf(selectedAnalyses.get(0)), 0, false);
            }
        }
    }

    @Override
    public void setFilterInView(AnalysisFilter filter) {
        toolBar.setFilterInView(filter);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        toolBar.asWidget().ensureDebugId(baseID + AnalysisModule.Ids.MENUBAR);
    }

    private void setSelectionCount(int count) {
        selectionStatus.setText(appearance.selectionCount(count));
    }
}
