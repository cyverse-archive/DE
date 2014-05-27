/**
 * 
 */
package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.client.events.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisCommentSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisParamValueSelectedEvent;
import org.iplantc.de.analysis.client.presenter.proxy.AnalysisRpcProxy;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamView;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamViewColumnModel;
import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * @author sriram
 * 
 */
public class AnalysesViewImpl extends Composite implements AnalysesView {

    private class AnalysisParameterKeyProvider implements ModelKeyProvider<AnalysisParameter> {

        @Override
        public String getKey(AnalysisParameter item) {
            return item.getId();
        }

    }

    private final class SelectionChangeHandlerImpl implements SelectionChangedHandler<Analysis> {
        @Override
        public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {
            setSelectionCount(event.getSelection().size());
        }
    }

    @UiTemplate("AnalysesViewImpl.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, AnalysesViewImpl> {
    }
    @UiField(provided = true)
    final ColumnModel<Analysis> cm;
    @UiField(provided = true)
    final ListStore<Analysis> listStore;
    @UiField
    BorderLayoutContainer con;
    @UiField
    Grid<Analysis> grid;
    @UiField
    LiveGridView<Analysis> gridView;
    @UiField
    BorderLayoutData northData;
    @UiField
    ToolBar toolBar;

    ViewMenu viewMenu;
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private final IplantDisplayStrings displayStrings;
    private final FileEditorServiceFacade fileEditorService;
    private final AnalysisParamViewColumnModel paramViewColumnModel;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader;

    private Presenter presenter;
    private final Status selectionStatus;

    @Inject
    public AnalysesViewImpl(final ListStore<Analysis> listStore, final AnalysisColumnModel cm, final AnalysisParamViewColumnModel paramViewColumnModel,
                            final CheckBoxSelectionModel<Analysis> checkBoxModel, final ViewMenu menuBar, final AnalysisRpcProxy proxy, final IplantDisplayStrings displayStrings, final FileEditorServiceFacade fileEditorService) {
        this.listStore = listStore;
        this.cm = cm;
        this.paramViewColumnModel = paramViewColumnModel;
        this.viewMenu = menuBar;
        this.displayStrings = displayStrings;
        this.fileEditorService = fileEditorService;
        initWidget(uiBinder.createAndBindUi(this));
        con.setNorthWidget(menuBar, northData);
        selectionStatus = new Status();

        loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>>(proxy);
        initLoader();

        toolBar.addStyleName(ThemeStyles.get().style().borderTop());
        toolBar.getElement().getStyle().setProperty("borderBottom", "none");


        initGrid(checkBoxModel);
        initGridView();

        initToolbar();

        addSelectionChangedHandler(viewMenu);
    }

    private void initToolbar() {
        toolBar.add(new LiveToolItem(grid));
        selectionStatus.setWidth(100);
        setSelectionCount(0);
        toolBar.add(new FillToolItem());
        toolBar.add(selectionStatus);
    }

    private void initGridView() {
        gridView.setEmptyText(I18N.DISPLAY.noAnalyses());
        gridView.setRowHeight(28);
        gridView.setForceFit(true);
    }

    private void initGrid(final CheckBoxSelectionModel<Analysis> checkBoxModel) {
        grid.setLoader(loader);
        grid.setSelectionModel(checkBoxModel);
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangeHandlerImpl());
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        grid.setLoadMask(true);
    }

    private void initLoader() {
        loader.useLoadConfig(new FilterPagingLoadConfigBean());
        loader.setRemoteSort(true);
        loader.setReuseLoadConfig(true);
    }

    @Override
    public HandlerRegistration addAnalysisAppSelectedEventHandler(AnalysisAppSelectedEvent.AnalysisAppSelectedEventHandler handler) {
        return ((AnalysisColumnModel)cm).addAnalysisAppSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addAnalysisCommentSelectedEventHandler(AnalysisCommentSelectedEvent.AnalysisCommentSelectedEventHandler handler) {
        return ((AnalysisColumnModel)cm).addAnalysisCommentSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addAnalysisNameSelectedEventHandler(AnalysisNameSelectedEvent.AnalysisNameSelectedEventHandler handler) {
        return ((AnalysisColumnModel)cm).addAnalysisNameSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addAnalysisParamValueSelectedEventHandler(AnalysisParamValueSelectedEvent.AnalysisParamValueSelectedEventHandler handler) {
        return paramViewColumnModel.addAnalysisParamValueSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addLoadHandler(
            LoadHandler<FilterPagingLoadConfig, PagingLoadResult<Analysis>> handler) {
        return loader.addLoadHandler(handler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public HandlerRegistration addSelectionChangedHandler(@SuppressWarnings("rawtypes") SelectionChangedHandler handler) {
        return grid.getSelectionModel().addSelectionChangedHandler(handler);
    }

    @Override
    public ListStore<Analysis> getListStore() {
        return listStore;
    }

    @Override
    public List<Analysis> getSelectedAnalyses() {
        return grid.getSelectionModel().getSelectedItems();
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
    public void filterByAnalysisId(String analysisId, String name) {
        viewMenu.filterByAnalysisId(analysisId, name);
    }

    @Override
    public void loadAnalyses() {
        loader.load(0, 200);
    }

    @Override
    public void removeFromStore(List<Analysis> items) {
        checkNotNull(items);
        checkArgument(!items.isEmpty(), "Collection should not be empty");

        for (Analysis a : items) {
            grid.getStore().remove(a);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        viewMenu.init(presenter, this, (PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>>) grid.getLoader());
    }

    @Override
    public void viewParams() {
        for (Analysis ana : getSelectedAnalyses()) {
            ListStore<AnalysisParameter> listStore = new ListStore<AnalysisParameter>( new AnalysisParameterKeyProvider());
            final AnalysisParamView apv = new AnalysisParamView(listStore, paramViewColumnModel, displayStrings, fileEditorService);
            apv.setHeading(displayStrings.viewParameters(ana.getName()));
            apv.addSaveAnalysisParametersEventHandler(presenter);
            apv.show();

            presenter.retrieveParameterData(ana, apv);
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        viewMenu.asWidget().ensureDebugId(baseID + AnalysisModule.Ids.MENUBAR);

    }

    private void setSelectionCount(int count) {
        selectionStatus.setText(count + " item(s)");
    }
}
