package org.iplantc.de.client.analysis.presenter;

import org.iplantc.de.client.analysis.util.AnalysisParameterValueParser;
import org.iplantc.de.client.analysis.views.AnalysesToolbarView;
import org.iplantc.de.client.analysis.views.AnalysesToolbarViewImpl;
import org.iplantc.de.client.analysis.views.AnalysesView;
import org.iplantc.de.client.analysis.views.AnalysisParamView;
import org.iplantc.de.client.analysis.views.cells.AnalysisParamNameCell;
import org.iplantc.de.client.analysis.views.cells.AnalysisParamValueCell;
import org.iplantc.de.client.analysis.widget.AnalysisSearchField;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.AnalysesAutoBeanFactory;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisExecutionStatus;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.analysis.AnalysisParametersList;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * A presenter for analyses view
 * 
 * @author sriram
 * 
 */
public class AnalysesPresenter implements AnalysesView.Presenter, AnalysesToolbarView.Presenter {

    private final AnalysesView view;
    private final AnalysesToolbarView toolbar;
    private final AnalysesAutoBeanFactory factory = GWT.create(AnalysesAutoBeanFactory.class);
    private HandlerRegistration handlerFirstLoad;
    private final EventBus eventBus;

    public AnalysesPresenter(final AnalysesView view, final EventBus eventBus) {
        this.view = view;
        this.eventBus = eventBus;
        this.view.setPresenter(this);
        toolbar = new AnalysesToolbarViewImpl();
        toolbar.setPresenter(this);
        view.setNorthWidget(toolbar);
        setRefreshButton(view.getRefreshButton());
        view.setLoader(initRemoteLoader());
    }

    @Override
    public void go(final HasOneWidget container, final List<Analysis> selectedAnalyses) {
        go(container);

        if (selectedAnalyses != null && !selectedAnalyses.isEmpty()) {
            handlerFirstLoad = view.addLoadHandler(new FirstLoadHandler(selectedAnalyses));
        }
    }

    @Override
    public void go(final HasOneWidget container) {
        container.setWidget(view.asWidget());
        view.loadAnalyses();
    }

    @Override
    public void onDeleteClicked() {
        if (view.getSelectedAnalyses().size() > 0) {
            final List<Analysis> execs = view.getSelectedAnalyses();

            ConfirmMessageBox cmb = new ConfirmMessageBox(org.iplantc.de.resources.client.messages.I18N.DISPLAY.warning(),
                    I18N.DISPLAY.analysesExecDeleteWarning());
            cmb.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
            cmb.addHideHandler(new DeleteMessageBoxHandler(execs));
            cmb.show();
        }

    }

    @Override
    public void onViewParamClicked() {
        for (Analysis ana : view.getSelectedAnalyses()) {
            ListStore<AnalysisParameter> listStore = new ListStore<AnalysisParameter>(
                    new AnalysisParameterKeyProvider());
            final AnalysisParamView apv = new AnalysisParamView(listStore, buildColumnModel());
            apv.mask();
            retrieveParameterData(ana.getId(), new AsyncCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    AutoBean<AnalysisParametersList> bean = AutoBeanCodex.decode(factory,
                            AnalysisParametersList.class, result);
                    apv.loadParameters(AnalysisParameterValueParser.parse(bean.as().getParameterList()));
                    apv.unmask();
                }

                @Override
                public void onFailure(Throwable caught) {
                    apv.unmask();
                    ErrorHandler.post(caught);

                }
            });
            apv.setHeading(org.iplantc.de.resources.client.messages.I18N.DISPLAY.viewParameters(ana.getName()));
            apv.show();
        }
    }

    @Override
    public void onAnalysisRelaunchClicked() {
        if (view.getSelectedAnalyses().size() != 1) {
            return;
        }
        Analysis selectedAnalysis = view.getSelectedAnalyses().get(0);
        if (selectedAnalysis.isAppDisabled()) {
            return;
        }
        AppWizardConfig config = ConfigFactory.appWizardConfig(selectedAnalysis.getAppId());
        config.setAnalysisId(selectedAnalysis);
        config.setRelaunchAnalysis(true);
        eventBus.fireEvent(new WindowShowRequestEvent(config));
    }

    private class AnalysisParameterKeyProvider implements ModelKeyProvider<AnalysisParameter> {

        @Override
        public String getKey(AnalysisParameter item) {
            return item.getId();
        }

    }

    private void setButtonState() {
        int selectionSize = 0;

        selectionSize = view.getSelectedAnalyses().size();

        switch (selectionSize) {
            case 0:
                toolbar.setCancelButtonEnabled(false);
                toolbar.setDeleteButtonEnabled(false);
                toolbar.setViewParamButtonEnabled(false);
                toolbar.setRelaunchAnalysisEnabled(false);
                break;

            case 1:
                enableCancelAnalysisButtonByStatus();
                toolbar.setDeleteButtonEnabled(true);
                toolbar.setViewParamButtonEnabled(true);
                Analysis selectedAnalysis = view.getSelectedAnalyses().get(0);
                if (selectedAnalysis.isAppDisabled()) {
                    toolbar.setRelaunchAnalysisEnabled(false);
                } else {
                    toolbar.setRelaunchAnalysisEnabled(true);
                }
                break;

            default:
                toolbar.setDeleteButtonEnabled(true);
                toolbar.setViewParamButtonEnabled(false);
                toolbar.setRelaunchAnalysisEnabled(false);
                enableCancelAnalysisButtonByStatus();
        }
    }

    private void enableCancelAnalysisButtonByStatus() {
        List<Analysis> aes = view.getSelectedAnalyses();
        boolean enable = false;
        for (Analysis ae : aes) {
            if (ae != null) {
                if (ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.SUBMITTED.toString()))
                        || ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.IDLE.toString()))
                        || ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.RUNNING.toString()))) {
                    enable = true;
                    break;
                }
            }
        }
        toolbar.setCancelButtonEnabled(enable);
    }

    private void retrieveParameterData(final String analysisId, final AsyncCallback<String> callback) {
        ServicesInjector.INSTANCE.getAnalysisServiceFacade().getAnalysisParams(analysisId, callback);
    }

    @Override
    public void onCancelClicked() {
        if (view.getSelectedAnalyses().size() > 0) {
            final List<Analysis> execs = view.getSelectedAnalyses();
            for (Analysis ae : execs) {
                if (ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.SUBMITTED.toString()))
                        || ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.IDLE.toString()))
                        || ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.RUNNING.toString()))) {
                    ServicesInjector.INSTANCE.getAnalysisServiceFacade().stopAnalysis(ae.getId(),
                            new CancelAnalysisServiceCallback(ae));
                }
            }
        }

    }

    @Override
    public void onAnalysesSelection(List<Analysis> selectedItems) {
        setButtonState();
    }

    @Override
    public void setRefreshButton(TextButton refreshBtn) {
        if (refreshBtn != null) {
            refreshBtn.setText(org.iplantc.de.resources.client.messages.I18N.DISPLAY.refresh());
            toolbar.setRefreshButton(refreshBtn);
        }
    }

    @SuppressWarnings("unchecked")
    private ColumnModel<AnalysisParameter> buildColumnModel() {
        AnalysisParameterProperties props = GWT.create(AnalysisParameterProperties.class);
        ColumnConfig<AnalysisParameter, AnalysisParameter> param_name = new ColumnConfig<AnalysisParameter, AnalysisParameter>(
                new IdentityValueProvider<AnalysisParameter>(), 175);
        param_name.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.paramName());
        param_name.setCell(new AnalysisParamNameCell());

        ColumnConfig<AnalysisParameter, ArgumentType> param_type = new ColumnConfig<AnalysisParameter, ArgumentType>(
                props.type(), 75);
        param_type.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.paramType());

        ColumnConfig<AnalysisParameter, AnalysisParameter> param_value = new ColumnConfig<AnalysisParameter, AnalysisParameter>(
                new IdentityValueProvider<AnalysisParameter>(), 325);
        param_value.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.paramValue());
        param_value.setCell(new AnalysisParamValueCell());

        List<ColumnConfig<AnalysisParameter, ?>> columns = new ArrayList<ColumnConfig<AnalysisParameter, ?>>();
        columns.addAll(Arrays.asList(param_name, param_type, param_value));

        return new ColumnModel<AnalysisParameter>(columns);
    }

    /**
     * Initializes the toolbar's PagingLoader for use in the AnalysesGrid with paging and filtering
     * support.
     */
    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> initRemoteLoader() {
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader = toolbar.getLoader();

        // KLUDGE PagingLoader uses a PagingLoadConfigBean by default, which causes an exception when it
        // tries to cast it to a FilterPagingLoadConfig on the initial load.
        loader.useLoadConfig(new FilterPagingLoadConfigBean());
        loader.setRemoteSort(true);
        loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, Analysis, PagingLoadResult<Analysis>>(
                view.getListStore()));

        return loader;

    }

    private final class DeleteSeviceCallback implements AsyncCallback<String> {
        private final List<Analysis> execs;
        private final List<Analysis> items_to_delete;

        private DeleteSeviceCallback(List<Analysis> items_to_delete, List<Analysis> execs) {
            this.execs = execs;
            this.items_to_delete = items_to_delete;
        }

        @Override
        public void onSuccess(String arg0) {
            updateGrid();

        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.deleteAnalysisError(), caught);
        }

        private void updateGrid() {
            view.removeFromStore(items_to_delete);

            if (items_to_delete == null || execs.size() != items_to_delete.size()) {
                AlertMessageBox amb = new AlertMessageBox(org.iplantc.de.resources.client.messages.I18N.DISPLAY.warning(),
                        org.iplantc.de.resources.client.messages.I18N.DISPLAY.analysesNotDeleted());
                amb.show();
            }
        }
    }

    private final class CancelAnalysisServiceCallback implements AsyncCallback<String> {

        private final Analysis ae;

        public CancelAnalysisServiceCallback(final Analysis ae) {
            this.ae = ae;
        }

        @Override
        public void onSuccess(String result) {
            NotifyInfo.displayWarning(org.iplantc.de.resources.client.messages.I18N.DISPLAY.analysisStopSuccess(ae.getName()));
        }

        @Override
        public void onFailure(Throwable caught) {
            /*
             * JDS Send generic error message. In the future, the "error_code" string should be parsed
             * from the JSON to provide more detailed user feedback.
             */
            ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.stopAnalysisError(ae.getName()), caught);
        }

    }

    private final class DeleteMessageBoxHandler implements HideHandler {
        private final List<Analysis> execs;
        private final List<Analysis> items_to_delete;

        private DeleteMessageBoxHandler(List<Analysis> execs) {
            this.execs = execs;
            items_to_delete = new ArrayList<Analysis>();
        }

        private String buildDeleteRequestBody(List<Analysis> execs) {
            JSONObject obj = new JSONObject();
            JSONArray items = new JSONArray();
            int count = 0;
            for (Analysis ae : execs) {
                if (ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.COMPLETED.toString()))
                        || ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.FAILED.toString()))) {
                    items.set(count++, new JSONString(ae.getId()));
                    items_to_delete.add(ae);
                }

            }
            obj.put("executions", items); //$NON-NLS-1$
            return obj.toString();
        }

        @Override
        public void onHide(HideEvent event) {
            ConfirmMessageBox cmb = (ConfirmMessageBox)event.getSource();
            if (cmb.getHideButton() == cmb.getButtonById(PredefinedButton.OK.name())) {
                String body = buildDeleteRequestBody(execs);
                ServicesInjector.INSTANCE.getAnalysisServiceFacade().deleteAnalysis(UserInfo.getInstance().getWorkspaceId(), body,
                        new DeleteSeviceCallback(items_to_delete, execs));
            }

        }
    }

    @Override
    public List<Analysis> getSelectedAnalyses() {
        return view.getSelectedAnalyses();
    }

    @Override
    public void setSelectedAnalyses(List<Analysis> selectedAnalyses) {
        if (selectedAnalyses == null || selectedAnalyses.isEmpty()) {
            return;
        }

        ListStore<Analysis> store = view.getListStore();
        ArrayList<Analysis> selectNow = Lists.newArrayList();

        for (Analysis select : selectedAnalyses) {
            Analysis storeModel = store.findModel(select);
            if (storeModel != null) {
                selectNow.add(storeModel);
            }
        }

        if (selectNow.isEmpty()) {
            Analysis first = selectedAnalyses.get(0);
            toolbar.getFilterField().filterByAnalysisId(first.getId(), first.getName());
        } else {
            view.setSelectedAnalyses(selectNow);
        }
    }

    /**
     * A LoadHandler needed to set selected analyses after the initial view load, since settings like
     * page size are only set in the reused config by the loader after an initial grid load, which may be
     * by-passed by the {@link AnalysisSearchField#filterByAnalysisId} call in
     * {@link AnalysesPresenter#setSelectedAnalyses}.
     * 
     * A benefit of selecting analyses with this LoadHandler is if the analysis to select has already
     * loaded when this handler is called, then it can be selected immediately without filtering.
     * 
     * @author psarando
     * 
     */
    private class FirstLoadHandler implements
            LoadHandler<FilterPagingLoadConfig, PagingLoadResult<Analysis>> {

        private final List<Analysis> selectedAnalyses;

        public FirstLoadHandler(List<Analysis> selectedAnalyses) {
            this.selectedAnalyses = selectedAnalyses;
        }

        @Override
        public void onLoad(LoadEvent<FilterPagingLoadConfig, PagingLoadResult<Analysis>> event) {
            handlerFirstLoad.removeHandler();
            setSelectedAnalyses(selectedAnalyses);
        }
    }
}
