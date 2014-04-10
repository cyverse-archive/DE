package org.iplantc.de.analysis.client.presenter;

import org.iplantc.de.analysis.client.views.AnalysesView;
import org.iplantc.de.analysis.client.views.AnalysisParamView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.AnalysesAutoBeanFactory;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisExecutionStatus;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A presenter for analyses view
 * 
 * @author sriram
 * 
 */
public class AnalysesPresenterImpl implements AnalysesView.Presenter {



    private final class CancelAnalysisServiceCallback implements AsyncCallback<String> {

        private final Analysis ae;

        public CancelAnalysisServiceCallback(final Analysis ae) {
            this.ae = ae;
        }

        @Override
        public void onFailure(Throwable caught) {
            /*
             * JDS Send generic error message. In the future, the "error_code" string should be parsed
             * from the JSON to provide more detailed user feedback.
             */
            ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.stopAnalysisError(ae.getName()), caught);
        }

        @Override
        public void onSuccess(String result) {
            NotifyInfo.displayWarning(org.iplantc.de.resources.client.messages.I18N.DISPLAY.analysisStopSuccess(ae.getName()));
        }

    }

    private final class DeleteMessageBoxHandler implements HideHandler {
        private final List<Analysis> execs;
        private final List<Analysis> items_to_delete;

        private DeleteMessageBoxHandler(List<Analysis> execs) {
            this.execs = execs;
            items_to_delete = new ArrayList<Analysis>();
        }

        @Override
        public void onHide(HideEvent event) {
            ConfirmMessageBox cmb = (ConfirmMessageBox)event.getSource();
            if (cmb.getHideButton() == cmb.getButtonById(PredefinedButton.OK.name())) {
                String body = buildDeleteRequestBody(execs);
                analysisService.deleteAnalysis(userInfo.getWorkspaceId(), body,
                                                      new DeleteServiceCallback(items_to_delete, execs));
            }

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
    }

    private final class DeleteServiceCallback implements AsyncCallback<String> {
        private final List<Analysis> execs;
        private final List<Analysis> items_to_delete;

        private DeleteServiceCallback(List<Analysis> items_to_delete, List<Analysis> execs) {
            this.execs = execs;
            this.items_to_delete = items_to_delete;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.deleteAnalysisError(), caught);
        }

        @Override
        public void onSuccess(String arg0) {
            updateGrid();

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

    /**
     * A LoadHandler needed to set selected analyses after the initial view load, since settings like
     * page size are only set in the reused config by the loader after an initial grid load, which may be
     * by-passed by the {@link org.iplantc.de.analysis.client.widget.AnalysisSearchField#filterByAnalysisId} call in
     * {@link AnalysesPresenterImpl#setSelectedAnalyses}.
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
    private final AnalysisServiceFacade analysisService;
    private final EventBus eventBus;
    //private final AnalysesToolbarView toolbar;
    private final AnalysesAutoBeanFactory factory;
    private final UserInfo userInfo;
    private final IplantAnnouncer announcer;
    private final AnalysesView view;
    private HandlerRegistration handlerFirstLoad;

    @Inject
    public AnalysesPresenterImpl(final AnalysesView view, final EventBus eventBus, final AnalysesAutoBeanFactory factory, final AnalysisServiceFacade analysisService, final UserInfo userInfo, final IplantAnnouncer announcer) {
        this.view = view;
        this.eventBus = eventBus;
        this.factory = factory;
        this.analysisService = analysisService;
        this.userInfo = userInfo;
        this.announcer = announcer;
        this.view.addSelectionChangedHandler(this);
        this.view.setPresenter(this);
    }

    @Override
    public void cancelSelectedAnalyses() {
        if (view.getSelectedAnalyses().size() > 0) {
            final List<Analysis> execs = view.getSelectedAnalyses();
            for (Analysis ae : execs) {
                if (ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.SUBMITTED.toString()))
                            || ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.IDLE.toString()))
                            || ae.getStatus().equalsIgnoreCase((AnalysisExecutionStatus.RUNNING.toString()))) {
                    analysisService.stopAnalysis(ae.getId(),
                                                        new CancelAnalysisServiceCallback(ae));
                }
            }
        }
    }

    @Override
    public void deleteSelectedAnalyses() {

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
            //toolbar.getFilterField().filterByAnalysisId(first.getId(), first.getName());
        } else {
            view.setSelectedAnalyses(selectNow);
        }
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
    public void goToSelectedAnalysisFolder() {

    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<Analysis> event) {

    }

    @Override
    public void relaunchSelectedAnalysis() {
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

    @Override
    public void renameSelectedAnalysis() {
        final List<Analysis> selectedAnalyses = getSelectedAnalyses();
        assert selectedAnalyses.size() == 1;

        // TODO CORE-5307 Obtain new name here. Probably via dialog.
        String newName = "";
        analysisService.renameAnalysis(selectedAnalyses.get(0), newName, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                announcer.schedule(new ErrorAnnouncementConfig(caught.getMessage()));
            }

            @Override
            public void onSuccess(Void result) {
                // TODO CORE-5307 Perform analysis rename here.
            }
        });
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId);
    }

    public void retrieveParameterData(final Analysis analysis, final AnalysisParamView apv){
        apv.mask();
        analysisService.getAnalysisParams(analysis, new AsyncCallback<List<AnalysisParameter>>() {
            @Override
            public void onSuccess(List<AnalysisParameter> result) {
                apv.loadParameters(result);
                apv.unmask();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                apv.unmask();
            }
        });
    }
}
