package org.iplantc.de.analysis.client.presenter;

import org.iplantc.de.analysis.client.events.*;
import org.iplantc.de.analysis.client.views.AnalysesView;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceStatMap;
import org.iplantc.de.client.models.diskResources.File;
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
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantPromptDialog;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * A presenter for analyses view
 *
 * @author sriram
 *
 */
public class AnalysesPresenterImpl implements AnalysesView.Presenter, AnalysisNameSelectedEvent.AnalysisNameSelectedEventHandler, AnalysisParamValueSelectedEvent.AnalysisParamValueSelectedEventHandler, AnalysisCommentSelectedEvent.AnalysisCommentSelectedEventHandler, AnalysisAppSelectedEvent.AnalysisAppSelectedEventHandler {

    private static class AnalysisCommentsDialog extends IPlantDialog {

        private final Analysis analysis;
        private final TextArea ta;

        public AnalysisCommentsDialog(final Analysis analysis, final IplantDisplayStrings displayStrings){
            this.analysis = analysis;

            String comments = analysis.getDescription();
            setHeadingText(displayStrings.comments());
            setSize("350px","300px");
            ta = new TextArea();
            ta.setValue(comments);
            add(ta);
        }

        public String getComment() {
            return ta.getValue();
        }

        public boolean isCommentChanged(){
            return !getComment().equals(analysis.getDescription());
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

    private class AnalysisParamSelectedStatCallback implements AsyncCallback<DiskResourceStatMap> {

        private final DiskResourceAutoBeanFactory factory;
        private final AnalysisParameter value;

        public AnalysisParamSelectedStatCallback(AnalysisParameter value, DiskResourceAutoBeanFactory factory) {
            this.value = value;
            this.factory = factory;
        }

        @Override
        public void onFailure(Throwable caught) {
            final SafeHtml message = SafeHtmlUtils.fromTrustedString(errorStrings.diskResourceDoesNotExist(value.getDisplayValue()));
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 3000));
        }

        @Override
        public void onSuccess(DiskResourceStatMap result) {
            final AutoBean<DiskResource> autoBean = AutoBeanUtils.getAutoBean(result.get(value.getDisplayValue()));
            final Splittable encode = AutoBeanCodex.encode(autoBean);
            File file = AutoBeanCodex.decode(factory, File.class, encode).as();
            eventBus.fireEvent(new ShowFilePreviewEvent(file, AnalysesPresenterImpl.this));
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
     * by-passed by the {@link org.iplantc.de.analysis.client.views.widget.AnalysisSearchField#filterByAnalysisId} call in
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

    interface Template extends SafeHtmlTemplates{
        @Template("Not yet implemented.<p>Waiting on completion of <a href='https://pods.iplantcollaborative.org/jira/browse/CORE-{0}' target=\"_blank\">CORE-{0}</a>")
        SafeHtml failMsg(String issueNumber);
    }

    Template unimplementedFailMessages = GWT.create(Template.class);

    private class RenameAnalysisCallback implements AsyncCallback<Void> {

        @Override
        public void onFailure(Throwable caught) {
            final SafeHtml message = unimplementedFailMessages.failMsg("5409");
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 5000));
        }

        @Override
        public void onSuccess(Void result) {
            // TODO CORE-5307 Perform analysis rename here.
        }
    }

    private class UpdateCommentsCallback implements AsyncCallback<Void> {
        @Override
        public void onFailure(Throwable caught) {
            SafeHtml message = unimplementedFailMessages.failMsg("5408");
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 5000));
        }

        @Override
        public void onSuccess(Void result) {

        }
    }

    private final AnalysisServiceFacade analysisService;
    private final IplantAnnouncer announcer;
    private final DiskResourceServiceFacade diskResourceService;
    private final FileEditorServiceFacade fileEditorService;
    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private final HasHandlers eventBus;
    private final AnalysesView view;
    private DiskResourceAutoBeanFactory drFactory;
    private final UserSessionServiceFacade userSessionService;
    private final UserInfo userInfo;
    private HandlerRegistration handlerFirstLoad;

    @Inject
    public AnalysesPresenterImpl(final AnalysesView view, final EventBus eventBus,
                                 final AnalysisServiceFacade analysisService,
                                 final IplantAnnouncer announcer,
                                 final IplantDisplayStrings displayStrings,
                                 final IplantErrorStrings errorStrings,
                                 final DiskResourceServiceFacade diskResourceService,
                                 final FileEditorServiceFacade fileEditorService,
                                 final DiskResourceAutoBeanFactory drFactory,
                                 final UserSessionServiceFacade userSessionService,
                                 final UserInfo userInfo) {
        this.view = view;
        this.eventBus = eventBus;
        this.analysisService = analysisService;
        this.announcer = announcer;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.diskResourceService = diskResourceService;
        this.fileEditorService = fileEditorService;
        this.drFactory = drFactory;
        this.userSessionService = userSessionService;
        this.userInfo = userInfo;
        this.view.addAnalysisNameSelectedEventHandler(this);
        this.view.addAnalysisParamValueSelectedEventHandler(this);
        this.view.addAnalysisCommentSelectedEventHandler(this);
        this.view.addAnalysisAppSelectedEventHandler(this);
        this.view.setPresenter(this);
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
    public void onRequestSaveAnalysisParameters(final SaveAnalysisParametersEvent event) {

        final IsMaskable maskable = event.getMaskable();
        maskable.mask(displayStrings.savingFileMask());
        fileEditorService.uploadTextAsFile(event.getPath(), event.getFileContents(), true, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(String result) {
                final Splittable split = StringQuoter.split(result);
                final File file = AutoBeanCodex.decode(drFactory, File.class, split.get("file")).as();
                eventBus.fireEvent(new FileSavedEvent(file));

                final Splittable annotatedFile = split.get("file");
                StringQuoter.create(DiskResourceUtil.parseParent(file.getPath())).assign(annotatedFile, "parentFolderId");
                StringQuoter.create(event.getPath()).assign(annotatedFile, "sourceUrl");

                final Splittable payload = StringQuoter.createSplittable();
                StringQuoter.create("file_uploaded").assign(payload, "action");
                annotatedFile.assign(payload, "data");

                final Splittable notificationMsg = StringQuoter.createSplittable();
                StringQuoter.create("data").assign(notificationMsg, "type");
                String subject = file.getName().isEmpty() ? errorStrings.importFailed(event.getPath())
                                         : displayStrings.fileUploadSuccess(file.getName());
                StringQuoter.create(subject).assign(notificationMsg, "subject");
                payload.assign(notificationMsg, "payload");
                StringQuoter.create(userInfo.getUsername()).assign(notificationMsg, "user");


                final String notificationMsgPayload = notificationMsg.getPayload();
                userSessionService.postClientNotification(JsonUtil.getObject(notificationMsgPayload), new AsyncCallback<String>(){
                    @Override
                    public void onFailure(Throwable caught) {
                        event.getHideable().hide();
                        announcer.schedule(new ErrorAnnouncementConfig(caught.getMessage()));
                    }

                    @Override
                    public void onSuccess(String result) {
                        event.getHideable().hide();
                        announcer.schedule(new SuccessAnnouncementConfig(displayStrings.importRequestSubmit(file.getName())));
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
        view.loadAnalyses();

        if (selectedAnalyses != null && !selectedAnalyses.isEmpty()) {
            handlerFirstLoad = view.addLoadHandler(new FirstLoadHandler(selectedAnalyses));
        }
    }

    @Override
    public void goToSelectedAnalysisFolder() {
        assert view.getSelectedAnalyses().size() == 1 : "There should be 1 and only 1 selected analysis.";
        // Request disk resource window
        eventBus.fireEvent(new OpenFolderEvent(view.getSelectedAnalyses().get(0).getResultFolderId()));
    }

    @Override
    public void onAnalysisAppSelected(AnalysisAppSelectedEvent event) {
        eventBus.fireEvent(new OpenAppForRelaunchEvent(event.getAnalysis()));
    }

    @Override
    public void onAnalysisCommentSelected(final AnalysisCommentSelectedEvent event) {
        // Show comments
        final AnalysisCommentsDialog d = new AnalysisCommentsDialog(event.getValue(), displayStrings);
        d.show();
        d.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent hideEvent) {
                if (PredefinedButton.OK.name().equals(d.getHideButton().getItemId())) {
                    if (d.isCommentChanged()) {
                        analysisService.updateAnalysisComments(event.getValue(), new UpdateCommentsCallback());
                    }
                }
            }
        });
    }

    @Override
    public void onAnalysisNameSelected(AnalysisNameSelectedEvent event) {
        // Request disk resource window
        eventBus.fireEvent(new OpenFolderEvent(event.getValue().getResultFolderId()));
    }

    @Override
    public void onAnalysisParamValueSelected(AnalysisParamValueSelectedEvent event) {

        final AnalysisParameter value = event.getValue();

        if(!ArgumentType.Input.equals(value.getType()))
            return;
        String infoType = value.getInfoType();
        if(infoType.equalsIgnoreCase("ReferenceGenome")
                   || infoType.equalsIgnoreCase("ReferenceSequence")
                   || infoType.equalsIgnoreCase("ReferenceAnnotation"))
            return;


        final DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);
        final HasPaths hasPaths = factory.pathsList().as();
        hasPaths.setPaths(Lists.newArrayList(value.getDisplayValue()));
        diskResourceService.getStat(hasPaths, new AnalysisParamSelectedStatCallback(value, factory));
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

        final IPlantPromptDialog dlg = new IPlantPromptDialog(displayStrings.rename(), -1, selectedAnalysis.getName(), new Validator<String>() {
            @Override
            public List<EditorError> validate(Editor<String> editor, String value) {
                return Collections.emptyList();
            }
        });
        dlg.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(!selectedAnalysis.getName().equals(dlg.getFieldText())){
                    analysisService.renameAnalysis(selectedAnalysis, dlg.getFieldText(), new RenameAnalysisCallback());
                }
            }
        });
        dlg.show();
    }

    public void retrieveParameterData(final Analysis analysis, final AnalysisParamView apv){
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
        checkState(selectedAnalyses.size() == 1, "There should only be 1 analysis selected, but there were %i", selectedAnalyses.size());

        final AnalysisCommentsDialog d = new AnalysisCommentsDialog(selectedAnalyses.get(0), displayStrings);
        d.show();
        d.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (PredefinedButton.OK.name().equals(d.getHideButton().getItemId())) {
                    if (d.isCommentChanged()){
                        analysisService.updateAnalysisComments(selectedAnalyses.get(0), new UpdateCommentsCallback());
                    }
                }
            }
        });
    }
}
