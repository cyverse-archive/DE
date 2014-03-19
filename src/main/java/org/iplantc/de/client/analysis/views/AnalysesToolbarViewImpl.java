/**
 * 
 */
package org.iplantc.de.client.analysis.views;

import org.iplantc.de.client.analysis.widget.AnalysisSearchField;
import org.iplantc.de.client.analysis.widget.proxy.AnalysisRpcProxy;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author sriram
 * 
 */
public class AnalysesToolbarViewImpl implements AnalysesToolbarView {

    private static AnalysesToolbarUiBinder uiBinder = GWT.create(AnalysesToolbarUiBinder.class);

    @UiTemplate("AnalysesToolbarView.ui.xml")
    interface AnalysesToolbarUiBinder extends UiBinder<Widget, AnalysesToolbarViewImpl> {
    }

    private final Widget widget;
    private Presenter presenter;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader;

    @UiField
    TextButton btnViewParam;

    @UiField
    TextButton btnCancel;

    @UiField
    TextButton btnDelete;

    @UiField
    TextButton btnRelaunchAnalysis;

    @UiField
    ToolBar menuToolBar;

    @UiField
    AnalysisSearchField filterField;

    @UiFactory
    AnalysisSearchField createSearchField() {
        return new AnalysisSearchField(loader);
    }

    public AnalysesToolbarViewImpl() {
        loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>>(
                new AnalysisRpcProxy());
        this.widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setDeleteButtonEnabled(boolean enabled) {
        btnDelete.setEnabled(enabled);
    }

    @Override
    public void setViewParamButtonEnabled(boolean enabled) {
        btnViewParam.setEnabled(enabled);
    }

    @Override
    public void setCancelButtonEnabled(boolean enabled) {
        btnCancel.setEnabled(enabled);
    }

    @Override
    public void setRelaunchAnalysisEnabled(boolean enabled) {
        btnRelaunchAnalysis.setEnabled(enabled);
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;

    }

    @UiHandler("btnDelete")
    void deleteClicked(SelectEvent event) {
        presenter.onDeleteClicked();
    }

    @UiHandler("btnCancel")
    void cancelClicked(SelectEvent event) {
        presenter.onCancelClicked();
    }

    @UiHandler("btnViewParam")
    void viewParamClicked(SelectEvent event) {
        presenter.onViewParamClicked();
    }

    @UiHandler("btnRelaunchAnalysis")
    void onRelaunchAnalysisClicked(SelectEvent event) {
        presenter.onAnalysisRelaunchClicked();
    }

    @Override
    public void setRefreshButton(TextButton refreshBtn) {
        menuToolBar.insert(refreshBtn, 1);

    }

    @Override
    public PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> getLoader() {
        return loader;
    }

    @Override
    public AnalysisSearchField getFilterField() {
        return filterField;
    }
}
