package org.iplantc.de.analysis.client.presenter;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.events.HTAnalysisExpandEvent;
import org.iplantc.de.analysis.client.events.OpenAppForRelaunchEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.selection.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.gin.factory.AnalysesViewFactory;
import org.iplantc.de.analysis.client.models.AnalysisFilter;
import org.iplantc.de.analysis.client.presenter.proxy.AnalysisRpcProxy;
import org.iplantc.de.analysis.client.presenter.sharing.AnalysisSharingPresenter;
import org.iplantc.de.analysis.client.views.AnalysisStepsView;
import org.iplantc.de.analysis.client.views.dialogs.AnalysisSharingDialog;
import org.iplantc.de.analysis.client.views.dialogs.AnalysisStepsInfoDialog;
import org.iplantc.de.analysis.client.views.sharing.AnalysisSharingViewImpl;
import org.iplantc.de.analysis.client.views.widget.AnalysisSearchField;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisStepsInfo;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * A presenter for analyses view
 * 
 * @author sriram, jstroot
 */
public class AnalysesPresenterImpl implements
                                  AnalysesView.Presenter,
                                  AnalysisNameSelectedEvent.AnalysisNameSelectedEventHandler,
                                  AnalysisAppSelectedEvent.AnalysisAppSelectedEventHandler,
                                  HTAnalysisExpandEvent.HTAnalysisExpandEventHandler {

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
            SafeHtml msg = SafeHtmlUtils.fromString(appearance.stopAnalysisError(ae.getName()));
            announcer.schedule(new ErrorAnnouncementConfig(msg, true, 3000));
        }

        @Override
        public void onSuccess(String result) {
            SafeHtml msg = SafeHtmlUtils.fromString(appearance.analysisStopSuccess(ae.getName()));
            announcer.schedule(new SuccessAnnouncementConfig(msg, true, 3000));
            loadAnalyses(currentFilter);
        }

    }

    /**
     * A LoadHandler needed to set selected analyses after the initial view load, since settings like
     * page size are only set in the reused config by the loader after an initial grid load, which may be
     * by-passed by the
     * {@link org.iplantc.de.analysis.client.views.widget.AnalysisSearchField#filterByAnalysisId} call in
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

    private class RenameAnalysisCallback implements AsyncCallback<Void> {

        private final Analysis selectedAnalysis;
        private final String newName;
        private final ListStore<Analysis> listStore;

        public RenameAnalysisCallback(Analysis selectedAnalysis,
                                      String newName,
                                      ListStore<Analysis> listStore) {
            this.selectedAnalysis = selectedAnalysis;
            this.newName = newName;
            this.listStore = listStore;
        }

        @Override
        public void onFailure(Throwable caught) {
            final SafeHtml message = appearance.analysisRenameFailed();
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 5000));
        }

        @Override
        public void onSuccess(Void result) {
            selectedAnalysis.setName(newName);
            listStore.update(selectedAnalysis);
            SafeHtml message = appearance.analysisRenameSuccess();
            announcer.schedule(new SuccessAnnouncementConfig(message, true, 5000));
        }
    }

    private class UpdateCommentsCallback implements AsyncCallback<Void> {
        private final Analysis selectedAnalysis;
        private final String newComment;
        private final ListStore<Analysis> listStore;

        public UpdateCommentsCallback(Analysis selectedAnalysis,
                                      String newComment,
                                      ListStore<Analysis> listStore) {
            this.selectedAnalysis = selectedAnalysis;
            this.newComment = newComment;
            this.listStore = listStore;
        }

        @Override
        public void onFailure(Throwable caught) {
            SafeHtml message = appearance.analysisCommentUpdateFailed();
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 5000));
        }

        @Override
        public void onSuccess(Void result) {
            selectedAnalysis.setComments(newComment);
            listStore.update(selectedAnalysis);
            SafeHtml message = appearance.analysisCommentUpdateSuccess();
            announcer.schedule(new SuccessAnnouncementConfig(message, true, 5000));
        }
    }

    @Inject
    AnalysisServiceFacade analysisService;
    @Inject
    IplantAnnouncer announcer;
    @Inject
    AnalysesView.Presenter.Appearance appearance;
    @Inject
    AnalysisStepsView analysisStepView;
    @Inject
    Provider<AnalysisSharingDialog> aSharingDialogProvider;

    @Inject
    CollaboratorsUtil collaboratorsUtil;
    @Inject
    JsonUtil jsonUtil;

    private AnalysisFilter currentFilter;

    private final ListStore<Analysis> listStore;

    private final AnalysesView view;
    private final HasHandlers eventBus;
    private HandlerRegistration handlerFirstLoad;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader;

    @Inject
    AnalysesPresenterImpl(final AnalysesViewFactory viewFactory,
                          final AnalysisRpcProxy proxy,
                          final ListStore<Analysis> listStore,
                          final EventBus eventBus) {
        this.listStore = listStore;
        this.eventBus = eventBus;
        loader = new PagingLoader<>(proxy);
        loader.useLoadConfig(new FilterPagingLoadConfigBean());
        loader.setRemoteSort(true);
        loader.setReuseLoadConfig(true);

        this.view = viewFactory.create(listStore, loader, this);

        this.view.addAnalysisNameSelectedEventHandler(this);
        this.view.addAnalysisAppSelectedEventHandler(this);
        this.view.addHTAnalysisExpandEventHandler(this);

        //Set default filter to ALL
      currentFilter = AnalysisFilter.ALL;
    }

    @Override
    public void onShareSupportSelected(List<Analysis> currentSelection,boolean shareWithInput) {

    }

    @Override
    public void cancelSelectedAnalyses(final List<Analysis> analysesToCancel) {
        for (Analysis analysis : analysesToCancel) {
            analysisService.stopAnalysis(analysis, new CancelAnalysisServiceCallback(analysis));
        }
    }

    @Override
    public void deleteSelectedAnalyses(final List<Analysis> analysesToDelete) {
        analysisService.deleteAnalyses(analysesToDelete, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(appearance.deleteAnalysisError(), caught);
            }

            @Override
            public void onSuccess(String arg0) {
                loadAnalyses(currentFilter);
            }
        });
    }

    @Override
    public List<Analysis> getSelectedAnalyses() {
        return view.getSelectedAnalyses();
    }

    @Override
    public void setSelectedAnalyses(final List<Analysis> selectedAnalyses) {
        if (selectedAnalyses == null || selectedAnalyses.isEmpty()) {
            return;
        }

        ArrayList<Analysis> selectNow = Lists.newArrayList();

        for (Analysis select : selectedAnalyses) {
            Analysis storeModel = listStore.findModel(select);
            if (storeModel != null) {
                selectNow.add(storeModel);
            }
        }

        if (selectNow.isEmpty()) {
            Analysis first = selectedAnalyses.get(0);
            view.filterByAnalysisId(first.getId(), first.getName());
        } else {
            view.setSelectedAnalyses(selectNow);
        }
    }

    @Override
    public void go(final HasOneWidget container, final List<Analysis> selectedAnalyses) {
        if (selectedAnalyses != null && !selectedAnalyses.isEmpty()) {
            handlerFirstLoad = loader.addLoadHandler(new FirstLoadHandler(selectedAnalyses));
        }
        loadAnalyses(AnalysisFilter.ALL);
        container.setWidget(view);
    }

    @Override
    public void loadAnalyses(AnalysisFilter filter) {
        FilterPagingLoadConfig config = loader.getLastLoadConfig();

        FilterConfigBean idParentFilter = new FilterConfigBean();
        idParentFilter.setField(AnalysisSearchField.PARENT_ID);
        idParentFilter.setValue("");

        FilterConfigBean filterCb = new FilterConfigBean();
        config.getFilters().clear();
        switch (filter) {
            case ALL:
                filterCb.setField("ownership");
                filterCb.setValue("all");
                break;
            case SHARED_WITH_ME:
                filterCb.setField("ownership");
                filterCb.setValue("theirs");
                break;

            case MY_ANALYSES:
                filterCb.setField("ownership");
                filterCb.setValue("mine");
                break;
        }
        config.getFilters().add(idParentFilter);
        config.getFilters().add(filterCb);
        config.setLimit(200);
        config.setOffset(0);
        loader.load(config);
    }

    @Override
    public void setFilterInView(AnalysisFilter filter) {
        view.setFilterInView(filter);
    }

    @Override
    public void goToSelectedAnalysisFolder(final Analysis selectedAnalysis) {
        // Request disk resource window
        eventBus.fireEvent(new OpenFolderEvent(selectedAnalysis.getResultFolderId(), true));
    }

    @Override
    public void onRefreshSelected() {
        loadAnalyses(currentFilter);
    }

    @Override
    public void onShowAllSelected() {
        loadAnalyses(AnalysisFilter.ALL);
    }

    @Override
    public void onShareSelected(List<Analysis> selected) {
        AnalysisSharingViewImpl sharingView = new AnalysisSharingViewImpl();
        AnalysisSharingPresenter sharingPresenter = new AnalysisSharingPresenter(analysisService,
                                                        selected,
                                                        sharingView,
                                                        collaboratorsUtil,
                                                        jsonUtil);
        AnalysisSharingDialog asd = aSharingDialogProvider.get();
        asd.setPresenter(sharingPresenter);
        asd.show();
    }

    @Override
    public void setCurrentFilter(AnalysisFilter filter) {
        if(filter == null) {
            currentFilter =filter;
            return;
        } else if(!(filter.equals(this.currentFilter))) {
            currentFilter = filter;
            loadAnalyses(currentFilter);
        }
    }

    @Override
    public AnalysisFilter getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public void onAnalysisAppSelected(AnalysisAppSelectedEvent event) {
        eventBus.fireEvent(new OpenAppForRelaunchEvent(event.getAnalysis()));
    }

    @Override
    public void onAnalysisNameSelected(AnalysisNameSelectedEvent event) {
        // Request disk resource window
        eventBus.fireEvent(new OpenFolderEvent(event.getValue().getResultFolderId(), true));
    }

    @Override
    public void relaunchSelectedAnalysis(final Analysis selectedAnalysis) {
        if (selectedAnalysis.isAppDisabled()) {
            return;
        }
        eventBus.fireEvent(new OpenAppForRelaunchEvent(selectedAnalysis));
    }

    @Override
    public void renameSelectedAnalysis(final Analysis selectedAnalysis, final String newName) {
        analysisService.renameAnalysis(selectedAnalysis,
                                       newName,
                                       new RenameAnalysisCallback(selectedAnalysis, newName, listStore));
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId);
    }

    @Override
    public void updateAnalysisComment(final Analysis value, final String comment) {
        analysisService.updateAnalysisComments(value, comment, new UpdateCommentsCallback(value,
                                                                                          comment,
                                                                                          listStore));
    }

    @Override
    public void onHTAnalysisExpanded(HTAnalysisExpandEvent event) {
        view.filterByParentAnalysisId(event.getValue().getId());
    }

    @Override
    public void getAnalysisStepInfo(Analysis value) {
        analysisService.getAnalysisSteps(value, new AsyncCallback<AnalysisStepsInfo>() {

            @Override
            public void onFailure(Throwable caught) {
                IplantAnnouncer.getInstance()
                               .schedule(new ErrorAnnouncementConfig(appearance.analysisStepInfoError()));

            }

            @Override
            public void onSuccess(AnalysisStepsInfo result) {
                AnalysisStepsInfoDialog asid = new AnalysisStepsInfoDialog(analysisStepView);
                asid.show();
                analysisStepView.clearData();
                analysisStepView.setData(result.getSteps());
            }
        });

    }
}
