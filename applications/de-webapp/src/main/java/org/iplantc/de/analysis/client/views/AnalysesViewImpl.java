/**
 * 
 */
package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.analysis.client.presenter.proxy.AnalysisRpcProxy;
import org.iplantc.de.client.desktop.widget.DEPagingToolbar;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.*;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.List;

/**
 * @author sriram
 * 
 */
public class AnalysesViewImpl extends Composite implements AnalysesView {

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiTemplate("AnalysesViewImpl.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, AnalysesViewImpl> {
    }

    @UiField(provided = true)
    final ListStore<Analysis> listStore;

    @UiField(provided = true)
    final ColumnModel<Analysis> cm;

    @UiField
    GridView<Analysis> gridView;

    @UiField
    Grid<Analysis> grid;

    @UiField
    FramedPanel mainPanel;

    @UiField
    BorderLayoutContainer con;

    @UiField
    BorderLayoutData northData;

    @UiField
    DEPagingToolbar toolBar;

    ViewMenu viewMenu;

    private Presenter presenter;

    @Inject
    public AnalysesViewImpl(final ListStore<Analysis> listStore, final AnalysisColumnModel cm,
                            final CheckBoxSelectionModel<Analysis> checkBoxModel, final ViewMenu menuBar, final AnalysisRpcProxy proxy) {
        this.listStore = listStore;
        this.cm = cm;
        this.viewMenu = menuBar;
        initWidget(uiBinder.createAndBindUi(this));
        con.setNorthWidget(menuBar);

        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>>(proxy);
        loader.useLoadConfig(new FilterPagingLoadConfigBean());
        loader.setRemoteSort(true);
        loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, Analysis, PagingLoadResult<Analysis>>(listStore));

        grid.setLoader(loader);
        toolBar.bind(loader);
        grid.setSelectionModel(checkBoxModel);
        grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        gridView.setEmptyText(I18N.DISPLAY.noAnalyses());
    }

    @Override
    public HandlerRegistration addSelectionChangedHandler(SelectionChangedHandler handler) {
        return grid.getSelectionModel().addSelectionChangedHandler(handler);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        viewMenu.asWidget().ensureDebugId(baseID + AnalysisModule.Ids.MENUBAR);

    }

    @Override
    public AnalysesView.ViewMenu getViewMenu() {
        return viewMenu;
    }

    @Override
    public void loadAnalyses() {
        grid.getLoader().load();
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
    public void removeFromStore(List<Analysis> items) {
        if (items != null & items.size() > 0) {
            for (Analysis a : items) {
                grid.getStore().remove(a);
            }
        }

    }

    @Override
    public ListStore<Analysis> getListStore() {
        return listStore;
    }

    @Override
    public HandlerRegistration addLoadHandler(
            LoadHandler<FilterPagingLoadConfig, PagingLoadResult<Analysis>> handler) {
        @SuppressWarnings("unchecked")
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader = (PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>>)grid.getLoader();

        return loader.addLoadHandler(handler);
    }
}
