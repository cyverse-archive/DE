package org.iplantc.de.analysis.client.presenter;

import org.iplantc.de.analysis.client.views.AnalysesView;
import org.iplantc.de.analysis.client.views.AnalysisParamView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.AnalysesAutoBeanFactory;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
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
            SafeHtml msg = SafeHtmlUtils.fromString(errorStrings.stopAnalysisError(ae.getName()));
            announcer.schedule(new ErrorAnnouncementConfig(msg, true, 3000));
        }

        @Override
        public void onSuccess(String result) {
            SafeHtml msg = SafeHtmlUtils.fromString(displayStrings.analysisStopSuccess(ae.getName()));
            announcer.schedule(new SuccessAnnouncementConfig(msg, true, 3000));
        }

    }

    private final class DeleteMessageBoxHandler implements HideHandler {
        private final List<Analysis> analysesToBeDeleted;

        private DeleteMessageBoxHandler(List<Analysis> analysesToBeDeleted) {
            this.analysesToBeDeleted = analysesToBeDeleted;
        }

        @Override
        public void onHide(HideEvent event) {
            ConfirmMessageBox cmb = (ConfirmMessageBox)event.getSource();
            if (cmb.getHideButton() == cmb.getButtonById(PredefinedButton.OK.name())) {
                analysisService.deleteAnalyses(analysesToBeDeleted, new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(errorStrings.deleteAnalysisError(), caught);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        view.removeFromStore(analysesToBeDeleted);
                    }
                });
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
    private final HasHandlers eventBus;
    private final AnalysesAutoBeanFactory factory;
    private final UserInfo userInfo;
    private final IplantAnnouncer announcer;
    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private final AnalysesView view;
    private HandlerRegistration handlerFirstLoad;

    @Inject
    public AnalysesPresenterImpl(final AnalysesView view, final EventBus eventBus, final AnalysesAutoBeanFactory factory, final AnalysisServiceFacade analysisService, final UserInfo userInfo, final IplantAnnouncer announcer, final IplantDisplayStrings displayStrings, final IplantErrorStrings errorStrings) {
        this.view = view;
        this.eventBus = eventBus;
        this.factory = factory;
        this.analysisService = analysisService;
        this.userInfo = userInfo;
        this.announcer = announcer;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.view.addSelectionChangedHandler(this);
        this.view.setPresenter(this);
    }

    @Override
    public void cancelSelectedAnalyses() {
        if (view.getSelectedAnalyses().size() <= 0) {
            return;
        }
        final List<Analysis> execs = view.getSelectedAnalyses();
        for (Analysis ae : execs) {
                analysisService.stopAnalysis(ae, new CancelAnalysisServiceCallback(ae));
        }
    }

    @Override
    public void deleteSelectedAnalyses() {

        if (view.getSelectedAnalyses().size() <= 0) {
            return;
        }
        final List<Analysis> analysesToBeDeleted = view.getSelectedAnalyses();

        ConfirmMessageBox cmb = new ConfirmMessageBox(displayStrings.warning(), displayStrings.analysesExecDeleteWarning());
        cmb.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        cmb.addHideHandler(new DeleteMessageBoxHandler(analysesToBeDeleted));
        cmb.show();
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
                final String message = caught.getMessage();
                SafeHtml msg = SafeHtmlUtils.fromString(message);
                announcer.schedule(new ErrorAnnouncementConfig(msg, true, 3000));
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

    @Override
    public void updateComments() {
        final List<Analysis> selectedAnalyses = getSelectedAnalyses();
        assert selectedAnalyses.size() == 1;

        analysisService.updateAnalysisComments(selectedAnalyses.get(0), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                final String message = caught.getMessage();
                SafeHtml msg = SafeHtmlUtils.fromString(message);
                announcer.schedule(new ErrorAnnouncementConfig(msg, true, 3000));
            }

            @Override
            public void onSuccess(Void result) {

            }
        });
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
