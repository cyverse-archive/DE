package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.AnalysisToolBarView;
import org.iplantc.de.analysis.client.events.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisCommentSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisParamValueSelectedEvent;
import org.iplantc.de.analysis.client.events.HTAnalysisExpandEvent.HTAnalysisExpandEventHandler;
import org.iplantc.de.analysis.client.gin.factory.AnalysisParamViewFactory;
import org.iplantc.de.analysis.client.gin.factory.AnalysisToolBarFactory;
import org.iplantc.de.analysis.client.presenter.proxy.AnalysisRpcProxy;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamView;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamViewColumnModel;
import org.iplantc.de.analysis.client.views.widget.AnalysisSearchField;
import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
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
public class AnalysesViewImpl extends Composite implements AnalysesView {

    private class AnalysisParameterKeyProvider implements ModelKeyProvider<AnalysisParameter> {

        @Override
        public String getKey(AnalysisParameter item) {
            return item.getId() + item.getDisplayValue();
        }

    }

    private final class SelectionChangeHandlerImpl implements SelectionChangedHandler<Analysis> {
        @Override
        public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {
            setSelectionCount(event.getSelection().size());
        }
    }

    @UiTemplate("AnalysesViewImpl.ui.xml")
    interface MyUiBinder extends UiBinder<BorderLayoutContainer, AnalysesViewImpl> { }

    @UiField(provided = true) final ColumnModel<Analysis> cm;
    @UiField(provided = true) final ListStore<Analysis> listStore;
    @Inject AnalysisParamViewFactory analysisParamViewFactory;
    @UiField Appearance appearance;
    @UiField Grid<Analysis> grid;
    @UiField LiveGridView<Analysis> gridView;
    @UiField ToolBar pagingToolBar;
    @UiField Status selectionStatus;
    @UiField(provided = true) AnalysisToolBarView toolBar;

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader;
    private final AnalysisParamViewColumnModel paramViewColumnModel;
    private final AnalysesView.Presenter presenter;

    @Inject
    AnalysesViewImpl(final ListStore<Analysis> listStore,
                     final AnalysisColumnModel cm,
                     final AnalysisParamViewColumnModel paramViewColumnModel,
                     final AnalysisToolBarFactory toolBarFactory,
                     final AnalysisRpcProxy proxy,
                     @Assisted AnalysesView.Presenter presenter) {
        this.listStore = listStore;
        this.cm = cm;
        this.paramViewColumnModel = paramViewColumnModel;
        this.presenter = presenter;

        // Init Loader
        loader = new PagingLoader<>(proxy);
        loader.useLoadConfig(new FilterPagingLoadConfigBean());
        loader.setRemoteSort(true);
        loader.setReuseLoadConfig(true);

        this.toolBar = toolBarFactory.create(presenter, this, loader);

        initWidget(uiBinder.createAndBindUi(this));

        pagingToolBar.addStyleName(appearance.pagingToolbarStyle());
        pagingToolBar.setBorders(false);

        // Init Grid
        grid.setLoader(loader);
        grid.setSelectionModel(new CheckBoxSelectionModel<>(new IdentityValueProvider<Analysis>()));
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangeHandlerImpl());
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        grid.setLoadMask(true);

        // Init Toolbar
        pagingToolBar.insert(new LiveToolItem(grid), 0);
        setSelectionCount(0);

        grid.getSelectionModel().addSelectionChangedHandler(toolBar);
    }

    //<editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration addAnalysisAppSelectedEventHandler(AnalysisAppSelectedEvent.AnalysisAppSelectedEventHandler handler) {
        return ((AnalysisColumnModel) cm).addAnalysisAppSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addAnalysisCommentSelectedEventHandler(AnalysisCommentSelectedEvent.AnalysisCommentSelectedEventHandler handler) {
        return ((AnalysisColumnModel) cm).addAnalysisCommentSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addAnalysisNameSelectedEventHandler(AnalysisNameSelectedEvent.AnalysisNameSelectedEventHandler handler) {
        return ((AnalysisColumnModel) cm).addAnalysisNameSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addAnalysisParamValueSelectedEventHandler(AnalysisParamValueSelectedEvent.AnalysisParamValueSelectedEventHandler handler) {
        return paramViewColumnModel.addAnalysisParamValueSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addHTAnalysisExpandEventHandler(HTAnalysisExpandEventHandler handler) {
        return ((AnalysisColumnModel) cm).addHTAnalysisExpandEventHandler(handler);
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
    public ListStore<Analysis> getListStore() {
        return listStore;
    }

    @Override
    public List<Analysis> getSelectedAnalyses() {
        return grid.getSelectionModel().getSelectedItems();
    }

    @Override
    public void loadAnalyses(boolean resetFilters) {
        FilterPagingLoadConfig config = loader.getLastLoadConfig();
        if (resetFilters) {
            // add only default filter
            FilterConfigBean idParentFilter = new FilterConfigBean();
            idParentFilter.setField(AnalysisSearchField.PARENT_ID);
            idParentFilter.setValue("");
            config.getFilters().clear();
            config.getFilters().add(idParentFilter);
        }
        config.setLimit(200);
        config.setOffset(0);
        loader.load(config);
    }

    @Override
    public void removeFromStore(List<Analysis> items) {
        checkNotNull(items);
        checkArgument(!items.isEmpty(), "Collection should not be empty");

        for (Analysis a : items) {
            grid.getStore().remove(a);
        }
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
    public void viewParams() {
        // FIXME Move dlg to toolbar, and service call from presenter method into Dlg
        for (Analysis ana : getSelectedAnalyses()) {
            // FIXME Toolbar only allows one analysis
            ListStore<AnalysisParameter> listStore = new ListStore<>(new AnalysisParameterKeyProvider());
            final AnalysisParamView apv = analysisParamViewFactory.createParamView(paramViewColumnModel, listStore);
            apv.setHeading(appearance.viewParameters(ana.getName()));
            apv.addSaveAnalysisParametersEventHandler(presenter);
            apv.show();

            presenter.retrieveParameterData(ana, apv);
        }
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
