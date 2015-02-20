package org.iplantc.de.analysis.client.presenter;

import static org.iplantc.de.client.models.apps.integration.ArgumentType.*;
import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.events.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisCommentSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisParamValueSelectedEvent;
import org.iplantc.de.analysis.client.events.HTAnalysisExpandEvent;
import org.iplantc.de.analysis.client.events.OpenAppForRelaunchEvent;
import org.iplantc.de.analysis.client.events.SaveAnalysisParametersEvent;
import org.iplantc.de.analysis.client.gin.factory.AnalysesViewFactory;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;

import static com.google.common.base.Preconditions.checkState;
import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;

import java.util.ArrayList;
import java.util.List;

/**
 * A presenter for analyses view
 * 
 * @author sriram, jstroot
 */
public class AnalysesPresenterImpl implements AnalysesView.Presenter,
                                              AnalysisNameSelectedEvent.AnalysisNameSelectedEventHandler,
                                              AnalysisParamValueSelectedEvent.AnalysisParamValueSelectedEventHandler,
                                              AnalysisCommentSelectedEvent.AnalysisCommentSelectedEventHandler,
                                              AnalysisAppSelectedEvent.AnalysisAppSelectedEventHandler,
                                              HTAnalysisExpandEvent.HTAnalysisExpandEventHandler {

    private static class AnalysisCommentsDialog extends IPlantDialog {

        private final Analysis analysis;
        private final TextArea ta;

        public AnalysisCommentsDialog(final Analysis analysis,
                                      final AnalysesView.Presenter.Appearance appearance) {
            this.analysis = analysis;

            String comments = analysis.getComments();
            setHeadingText(appearance.comments());
            setSize("350px", "300px");
            ta = new TextArea();
            ta.setValue(comments);
            add(ta);
        }

        public String getComment() {
            return ta.getValue();
        }

        public boolean isCommentChanged() {
            return !getComment().equals(analysis.getComments());
        }
    }

    private static class GetAnalysisParametersCallback implements AsyncCallback<List<AnalysisParameter>> {
        private final AnalysisParamView apv;

        public GetAnalysisParametersCallback(AnalysisParamView apv) {
            this.apv = apv;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
            apv.unmask();
        }

        @Override
        public void onSuccess(List<AnalysisParameter> result) {
            apv.loadParameters(result);
            apv.unmask();
        }
    }

    private class AnalysisParamSelectedStatCallback implements AsyncCallback<FastMap<DiskResource>> {

        private final AnalysisParameter value;

        public AnalysisParamSelectedStatCallback(AnalysisParameter value) {
            this.value = value;
        }

        @Override
        public void onFailure(Throwable caught) {
            final SafeHtml message = SafeHtmlUtils.fromTrustedString(appearance.diskResourceDoesNotExist(value.getDisplayValue()));
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 3000));
        }

        @Override
        public void onSuccess(FastMap<DiskResource> result) {
            eventBus.fireEvent(new ShowFilePreviewEvent((File)result.get(value.getDisplayValue()),
                                                        AnalysesPresenterImpl.this));
        }
    }

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
            loadAnalyses(false);
        }

    }

    private final class DeleteMessageBoxHandler implements DialogHideEvent.DialogHideHandler {
        private final List<Analysis> analysesToBeDeleted;

        private DeleteMessageBoxHandler(List<Analysis> analysesToBeDeleted) {
            this.analysesToBeDeleted = analysesToBeDeleted;
        }

        @Override
        public void onDialogHide(DialogHideEvent event) {
            if (PredefinedButton.OK.equals(event.getHideButton())) {
                analysisService.deleteAnalyses(analysesToBeDeleted, new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(appearance.deleteAnalysisError(), caught);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        loadAnalyses(false);
                    }
                });
            }
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
    private class FirstLoadHandler implements LoadHandler<FilterPagingLoadConfig, PagingLoadResult<Analysis>> {

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

    @Inject AnalysisServiceFacade analysisService;
    @Inject IplantAnnouncer announcer;
    @Inject DiskResourceServiceFacade diskResourceService;
    @Inject FileEditorServiceFacade fileEditorService;
    @Inject DiskResourceAutoBeanFactory drFactory;
    @Inject UserSessionServiceFacade userSessionService;
    @Inject UserInfo userInfo;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject JsonUtil jsonUtil;
    @Inject AnalysesView.Presenter.Appearance appearance;

    private final AnalysesView view;
    private final HasHandlers eventBus;
    private HandlerRegistration handlerFirstLoad;

    @Inject
    AnalysesPresenterImpl(final AnalysesViewFactory viewFactory,
                          final EventBus eventBus) {
        this.view = viewFactory.create(this);
        this.eventBus = eventBus;

        this.view.addAnalysisNameSelectedEventHandler(this);
        this.view.addAnalysisParamValueSelectedEventHandler(this);
        this.view.addAnalysisCommentSelectedEventHandler(this);
        this.view.addAnalysisAppSelectedEventHandler(this);
        this.view.addHTAnalysisExpandEventHandler(this);
    }

    @Override
    public void cancelSelectedAnalyses() {
        assert view.getSelectedAnalyses().size() > 0 : "Selection should be greater than 0";

        final List<Analysis> execs = view.getSelectedAnalyses();
        for (Analysis ae : execs) {
            analysisService.stopAnalysis(ae, new CancelAnalysisServiceCallback(ae));
        }
    }

    @Override
    public void deleteSelectedAnalyses() {
        assert view.getSelectedAnalyses().size() > 0 : "Selection should be greater than 0";

        final List<Analysis> analysesToBeDeleted = view.getSelectedAnalyses();

        ConfirmMessageBox cmb = new ConfirmMessageBox(appearance.warning(),
                                                      appearance.analysesExecDeleteWarning());
        cmb.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        cmb.addDialogHideHandler(new DeleteMessageBoxHandler(analysesToBeDeleted));
        cmb.show();
    }

    @Override
    public List<Analysis> getSelectedAnalyses() {
        return view.getSelectedAnalyses();
    }

    @Override
    public void onRequestSaveAnalysisParameters(final SaveAnalysisParametersEvent event) {

        final IsMaskable maskable = event.getMaskable();
        maskable.mask(appearance.savingFileMask());
        fileEditorService.uploadTextAsFile(event.getPath(),
                                           event.getFileContents(),
                                           true,
                                           new AsyncCallback<String>() {
                                               @Override
                                               public void onFailure(Throwable caught) {

                                               }

                                               @Override
                                               public void onSuccess(String result) {
                                                   final Splittable split = StringQuoter.split(result);
                                                   final File file = AutoBeanCodex.decode(drFactory,
                                                                                          File.class,
                                                                                          split.get("file"))
                                                                                  .as();
                                                   eventBus.fireEvent(new FileSavedEvent(file));

                                                   final Splittable annotatedFile = split.get("file");
                                                   StringQuoter.create(diskResourceUtil.parseParent(file.getPath()))
                                                               .assign(annotatedFile, "parentFolderId");
                                                   StringQuoter.create(event.getPath())
                                                               .assign(annotatedFile, "sourceUrl");

                                                   final Splittable payload = StringQuoter.createSplittable();
                                                   StringQuoter.create("file_uploaded").assign(payload,
                                                                                               "action");
                                                   annotatedFile.assign(payload, "data");

                                                   final Splittable notificationMsg = StringQuoter.createSplittable();
                                                   StringQuoter.create("data").assign(notificationMsg,
                                                                                      "type");
                                                   String subject = file.getName().isEmpty() ? appearance.importFailed(event.getPath())
                                                                                            : appearance.fileUploadSuccess(file.getName());
                                                   StringQuoter.create(subject).assign(notificationMsg,
                                                                                       "subject");
                                                   payload.assign(notificationMsg, "payload");
                                                   StringQuoter.create(userInfo.getUsername())
                                                               .assign(notificationMsg, "user");

                                                   final String notificationMsgPayload = notificationMsg.getPayload();
                                                   userSessionService.postClientNotification(jsonUtil.getObject(notificationMsgPayload),
                                                                                             new AsyncCallback<String>() {
                                                                                                 @Override
                                                                                                 public void
                                                                                                         onFailure(Throwable caught) {
                                                                                                     event.getHideable()
                                                                                                          .hide();
                                                                                                     announcer.schedule(new ErrorAnnouncementConfig(caught.getMessage()));
                                                                                                 }

                                                                                                 @Override
                                                                                                 public void
                                                                                                         onSuccess(String result) {
                                                                                                     event.getHideable()
                                                                                                          .hide();
                                                                                                     announcer.schedule(new SuccessAnnouncementConfig(appearance.importRequestSubmit(file.getName())));
                                                                                                 }
                                                                                             });
                                               }
                                           });

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
            view.filterByAnalysisId(first.getId(), first.getName());
        } else {
            view.setSelectedAnalyses(selectNow);
        }
    }

    @Override
    public void go(final HasOneWidget container, final List<Analysis> selectedAnalyses) {
        container.setWidget(view.asWidget());
        loadAnalyses(true);

        if (selectedAnalyses != null && !selectedAnalyses.isEmpty()) {
            handlerFirstLoad = view.addLoadHandler(new FirstLoadHandler(selectedAnalyses));
        }
    }

    @Override
    public void loadAnalyses(boolean resetFilters) {
        view.loadAnalyses(resetFilters);
    }

    @Override
    public void goToSelectedAnalysisFolder() {
        assert view.getSelectedAnalyses().size() == 1 : "There should be 1 and only 1 selected analysis.";
        // Request disk resource window
        eventBus.fireEvent(new OpenFolderEvent(view.getSelectedAnalyses().get(0).getResultFolderId(), true));
    }

    @Override
    public void onAnalysisAppSelected(AnalysisAppSelectedEvent event) {
        eventBus.fireEvent(new OpenAppForRelaunchEvent(event.getAnalysis()));
    }

    @Override
    public void onAnalysisCommentSelected(final AnalysisCommentSelectedEvent event) {
        // Show comments
        final AnalysisCommentsDialog d = new AnalysisCommentsDialog(event.getValue(), appearance);
        d.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent hideEvent) {
                if (PredefinedButton.OK.equals(hideEvent.getHideButton()) && d.isCommentChanged()) {
                    analysisService.updateAnalysisComments(event.getValue(),
                                                           d.getComment(),
                                                           new UpdateCommentsCallback(event.getValue(),
                                                                                      d.getComment(),
                                                                                      view.getListStore()));
                }
            }
        });
        d.show();
    }

    @Override
    public void onAnalysisNameSelected(AnalysisNameSelectedEvent event) {
        // Request disk resource window
        eventBus.fireEvent(new OpenFolderEvent(event.getValue().getResultFolderId(), true));
    }

    @Override
    public void onAnalysisParamValueSelected(AnalysisParamValueSelectedEvent event) {

        final AnalysisParameter value = event.getValue();

        if (!((Input.equals(value.getType())
                   || FileInput.equals(value.getType())
                   || FolderInput.equals(value.getType())
                   || FileFolderInput.equals(value.getType())
                   || MultiFileSelector.equals(value.getType())))) {
            return;
        }
        String infoType = value.getInfoType();
        if (infoType.equalsIgnoreCase("ReferenceGenome")
                || infoType.equalsIgnoreCase("ReferenceSequence")
                || infoType.equalsIgnoreCase("ReferenceAnnotation")) {
            return;
        }

        final File hasPath = drFactory.file().as();
        String path = value.getDisplayValue();
        hasPath.setPath(path);
        diskResourceService.getStat(diskResourceUtil.asStringPathTypeMap(Lists.newArrayList(hasPath),
                                                                         TYPE.FILE),
                                    new AnalysisParamSelectedStatCallback(value));
    }

    @Override
    public void relaunchSelectedAnalysis() {
        assert view.getSelectedAnalyses().size() == 1 : "There should be 1 and only 1 selected analysis.";
        Analysis selectedAnalysis = view.getSelectedAnalyses().get(0);
        if (selectedAnalysis.isAppDisabled()) {
            return;
        }
        eventBus.fireEvent(new OpenAppForRelaunchEvent(selectedAnalysis));
    }

    @Override
    public void renameSelectedAnalysis() {
        assert view.getSelectedAnalyses().size() == 1 : "There should be 1 and only 1 selected analysis.";
        final Analysis selectedAnalysis = view.getSelectedAnalyses().get(0);

        final IPlantPromptDialog dlg = new IPlantPromptDialog(appearance.rename(),
                                                              -1,
                                                              selectedAnalysis.getName(),
                                                              new DiskResourceNameValidator());
        dlg.setHeadingText(appearance.renameAnalysis());
        dlg.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if (!selectedAnalysis.getName().equals(dlg.getFieldText())) {
                    analysisService.renameAnalysis(selectedAnalysis,
                                                   dlg.getFieldText(),
                                                   new RenameAnalysisCallback(selectedAnalysis,
                                                                              dlg.getFieldText(),
                                                                              view.getListStore()));
                }
            }
        });
        dlg.show();
    }

    @Override
    public void retrieveParameterData(final Analysis analysis, final AnalysisParamView apv) {
        apv.mask();
        analysisService.getAnalysisParams(analysis, new GetAnalysisParametersCallback(apv));
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId);
    }

    @Override
    public void updateComments() {
        final List<Analysis> selectedAnalyses = view.getSelectedAnalyses();
        checkState(selectedAnalyses.size() == 1,
                   "There should only be 1 analysis selected, but there were %i",
                   selectedAnalyses.size());

        final AnalysisCommentsDialog d = new AnalysisCommentsDialog(selectedAnalyses.get(0),
                                                                    appearance);
        d.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (PredefinedButton.OK.equals(event.getHideButton()) && d.isCommentChanged()) {
                    analysisService.updateAnalysisComments(selectedAnalyses.get(0),
                                                           d.getComment(),
                                                           new UpdateCommentsCallback(selectedAnalyses.get(0),
                                                                                      d.getComment(),
                                                                                      view.getListStore()));
                }
            }
        });
        d.show();
    }

    @Override
    public void onHTAnalysisExpanded(HTAnalysisExpandEvent event) {
        view.filterByParentAnalysisId(event.getValue().getId());
    }
}
