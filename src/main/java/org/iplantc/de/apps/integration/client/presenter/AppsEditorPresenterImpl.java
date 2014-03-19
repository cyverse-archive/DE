package org.iplantc.de.apps.integration.client.presenter;

import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.integration.client.dialogs.CommandLineOrderingPanel;
import org.iplantc.de.apps.integration.client.events.DeleteArgumentEvent;
import org.iplantc.de.apps.integration.client.events.DeleteArgumentEvent.DeleteArgumentEventHandler;
import org.iplantc.de.apps.integration.client.events.DeleteArgumentGroupEvent;
import org.iplantc.de.apps.integration.client.events.UpdateCommandLinePreviewEvent;
import org.iplantc.de.apps.integration.client.gin.AppsEditorInjector;
import org.iplantc.de.apps.integration.client.presenter.visitors.DeleteArgumentGroup;
import org.iplantc.de.apps.integration.client.presenter.visitors.GatherAllEventProviders;
import org.iplantc.de.apps.integration.client.presenter.visitors.InitLabelOnlyEditMode;
import org.iplantc.de.apps.integration.client.presenter.visitors.InitializeArgumentEventManagement;
import org.iplantc.de.apps.integration.client.presenter.visitors.InitializeArgumentGroupEventManagement;
import org.iplantc.de.apps.integration.client.presenter.visitors.InitializeDragAndDrop;
import org.iplantc.de.apps.integration.client.presenter.visitors.RegisterEventHandlers;
import org.iplantc.de.apps.integration.client.view.AppsEditorView;
import org.iplantc.de.apps.widgets.client.events.ArgumentAddedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentAddedEvent.ArgumentAddedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent.ArgumentGroupAddedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.AppLaunchPreviewViewImpl;
import org.iplantc.de.apps.widgets.client.view.AppLaunchView.RenameWindowHeaderCommand;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.IsMinimizable;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.DataObject;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.UUIDServiceAsync;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IplantInfoBox;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.resources.client.uiapps.integration.AppIntegrationErrorMessages;
import org.iplantc.de.resources.client.uiapps.integration.AppIntegrationMessages;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author jstroot
 * 
 */
public class AppsEditorPresenterImpl implements AppsEditorView.Presenter, DeleteArgumentEventHandler, ArgumentAddedEventHandler, ArgumentGroupAddedEventHandler {

    /**
     * This dialog is used when the user attempts to close the view when the current AppTemplate contains
     * errors
     * 
     * @author jstroot
     * 
     */
    private final class ContainsErrorsOnHideDialog extends IplantInfoBox {
        private final HandlerRegistration beforeHideHndlrReg;
        private final Component component;

        private ContainsErrorsOnHideDialog(IplantDisplayStrings messages, IplantErrorStrings errorMessages, Component component, HandlerRegistration beforeHideHndlrReg) {
            super(messages.warning(), errorMessages.appContainsErrorsPromptToContinue());
            this.component = component;
            this.beforeHideHndlrReg = beforeHideHndlrReg;
            setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
            setIcon(MessageBox.ICONS.error());
        }

        @Override
        protected void onButtonPressed(TextButton button) {
            if (button == getButtonBar().getItemByItemId(PredefinedButton.YES.name())) {
                // JDS Abort current AppTemplate and hide.
                beforeHideHndlrReg.removeHandler();
                component.hide();
            }
            hide();
        }

    }
    /**
     * This dialog is used when the user is attempting to edit a new AppTemplate, but the existing
     * AppTemplate contains errors.
     * 
     * @author jstroot
     * 
     */
    private final class ContainsErrorsOnSwitchDialog extends IplantInfoBox {
        private final AppTemplate appTempl;
        private final HasOneWidget container;
        private final RenameWindowHeaderCommand renameHeaderCmd;

        private ContainsErrorsOnSwitchDialog(IplantDisplayStrings messages, IplantErrorStrings errorMessages, HasOneWidget container, AppTemplate appTemplate, RenameWindowHeaderCommand renameCmd) {
            super(messages.warning(), errorMessages.appContainsErrorsPromptToContinue());
            this.container = container;
            this.appTempl = appTemplate;
            this.renameHeaderCmd = renameCmd;
            setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
            setIcon(MessageBox.ICONS.error());
        }

        @Override
        protected void onButtonPressed(TextButton button) {
            if (button == getButtonBar().getItemByItemId(PredefinedButton.YES.name())) {
                // JDS Abort current AppTemplate and switch.
                AppsEditorPresenterImpl.this.appTemplate = appTempl;
                AppsEditorPresenterImpl.this.go(container);
                if (renameHeaderCmd != null) {
                    renameHeaderCmd.execute();
                }
            }
            hide();
        }
    }
    /**
     * This callback is used to apply any necessary UUIDs to an AppTemplate's arguments before
     * updating/saving the AppTemplate.
     * 
     * @author jstroot
     * 
     */
    private final class GetUuidThenDoSaveCallback implements AsyncCallback<List<String>> {
        private final List<Argument> argNeedUuid;
        private final AsyncCallback<Void> onSaveCallback;
        private final AppTemplate toBeSaved;

        private GetUuidThenDoSaveCallback(List<Argument> argNeedUuid, AppTemplate toBeSaved, AsyncCallback<Void> onSaveCallback) {
            this.argNeedUuid = argNeedUuid;
            this.toBeSaved = toBeSaved;
            this.onSaveCallback = onSaveCallback;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(List<String> result) {
            if ((result == null) || (result.size() != argNeedUuid.size())) {
                return;
            }
            // Apply UUIDs
            for (Argument arg : argNeedUuid) {
                arg.setId(result.remove(0));
            }
            doSave(toBeSaved, onSaveCallback);
        }
    }
    /**
     * This dialog is used when the user attempts to close the view or click "Save" when the current
     * AppTemplate contains unsaved changes.
     * 
     * @author jstroot
     * 
     */
    private final class PromptForSaveDialog extends IplantInfoBox {
        private final HandlerRegistration beforeHideHndlrReg;
        private final Component component;

        private PromptForSaveDialog(IplantDisplayStrings messages, Component component, HandlerRegistration beforeHideHndlrReg) {
            super(messages.save(), messages.unsavedChanges());
            setIcon(MessageBox.ICONS.question());
            setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
            this.component = component;
            this.beforeHideHndlrReg = beforeHideHndlrReg;
        }

        @Override
        protected void onButtonPressed(TextButton button) {
            if (button == getButtonBar().getItemByItemId(PredefinedButton.YES.name())) {
                // JDS Do save and let window close
                beforeHideHndlrReg.removeHandler();
                doOnSaveClicked(null);
                component.hide();
            } else if (button == getButtonBar().getItemByItemId(PredefinedButton.NO.name())) {
                // JDS Just let window close
                beforeHideHndlrReg.removeHandler();
                component.hide();
            }
            hide();
        }
    }
    /**
     * This dialog is used when the user is attempting to edit a new AppTemplate, but the current
     * AppTemplate has unsaved changes.
     * 
     * @author jstroot
     * 
     */
    private final class PromptForSaveThenSwitchDialog extends IplantInfoBox {
        private final AppTemplate appTempl;
        private final HasOneWidget container;
        private final RenameWindowHeaderCommand renameHeaderCmd;

        private PromptForSaveThenSwitchDialog(IplantDisplayStrings messages, HasOneWidget container, AppTemplate appTemplate, RenameWindowHeaderCommand renameCmd) {
            super(messages.save(), messages.unsavedChanges());
            setIcon(MessageBox.ICONS.question());
            setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
            this.container = container;
            this.appTempl = appTemplate;
            this.renameHeaderCmd = renameCmd;
        }

        @Override
        protected void onButtonPressed(TextButton button) {
            if (button == getButtonBar().getItemByItemId(PredefinedButton.YES.name())) {
                // Perform save, then switch.
                doOnSaveClicked(new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {/* Do Nothing */}

                    @Override
                    public void onSuccess(Void result) {
                        AppsEditorPresenterImpl.this.appTemplate = appTempl;
                        AppsEditorPresenterImpl.this.go(container);
                        if (renameHeaderCmd != null) {
                            renameHeaderCmd.execute();
                        }
                    }
                });
            } else if (button == getButtonBar().getItemByItemId(PredefinedButton.NO.name())) {
                // JDS Abort current changes to AppTemplate and switch.
                AppsEditorPresenterImpl.this.appTemplate = appTempl;
                AppsEditorPresenterImpl.this.go(container);
                if (renameHeaderCmd != null) {
                    renameHeaderCmd.execute();
                }
            }
            hide();
        }
    }
    public static native void doJsonFormattting(XElement textArea,String val,int width, int height) /*-{
		var myCodeMirror = $wnd.CodeMirror(textArea, {
			value : val,
			mode : {
				name : "javascript",
				json : true
			}
		});
		myCodeMirror.setOption("lineWrapping", false);
		myCodeMirror.setSize(width, height);
		myCodeMirror.setOption("readOnly", true);
    }-*/;
    private final AppTemplateWizardAppearance appearance;
    private final AppIntegrationMessages appIntMessages = GWT.create(AppIntegrationMessages.class);
    private AppTemplate appTemplate;
    private final AppTemplateServices atService;
    private HandlerRegistration beforeHideHandlerRegistration;
    private final AppIntegrationErrorMessages errorMessages;
    private final EventBus eventBus;
    private AppTemplate lastSave;

    private final IplantDisplayStrings messages;

    private boolean onlyLabelEditMode = false;

    private boolean postEdit = false;

    private RenameWindowHeaderCommand renameCmd;

    private final UUIDServiceAsync uuidService;

    private final AppsEditorView view;
    private final IplantAnnouncer announcer;

    @Inject
    public AppsEditorPresenterImpl(final AppsEditorView view, final EventBus eventBus, final AppTemplateServices atService, final AppIntegrationErrorMessages errorMessages,
            final IplantDisplayStrings messages, final UUIDServiceAsync uuidService, final AppTemplateWizardAppearance appearance, final IplantAnnouncer announcer) {
        this.view = view;
        this.eventBus = eventBus;
        this.atService = atService;
        this.errorMessages = errorMessages;
        this.messages = messages;
        this.uuidService = uuidService;
        this.appearance = appearance;
        this.announcer = announcer;
        view.setPresenter(this);
    }

    @Override
    public void doArgumentDelete(DeleteArgumentEvent event) {
        AutoBean<Argument> autoBean = AutoBeanUtils.getAutoBean(event.getArgumentToBeDeleted());

        // Remove all handlers store in the autobean
        if (autoBean.getTag(AppsEditorView.Presenter.HANDLERS) != null) {
            List<HandlerRegistration> handlerRegs = autoBean.getTag(AppsEditorView.Presenter.HANDLERS);
            for (HandlerRegistration hr : handlerRegs) {
                hr.removeHandler();
            }
        }
    }

    @Override
    public void doArgumentGroupDelete(DeleteArgumentGroupEvent event) {
        AppTemplate flush = view.flush();
        if (flush.getArgumentGroups().size() == 1) {
            announcer.schedule(new ErrorAnnouncementConfig(errorMessages.cannotDeleteLastArgumentGroup(), true, 3000));
            return;
        }
        AutoBean<ArgumentGroup> autoBean = AutoBeanUtils.getAutoBean(event.getArgumentGroupToBeDeleted());

        // Remove all handlers store in the autobean
        if (autoBean.getTag(AppsEditorView.Presenter.HANDLERS) != null) {
            List<HandlerRegistration> handlerRegs = autoBean.getTag(AppsEditorView.Presenter.HANDLERS);
            for (HandlerRegistration hr : handlerRegs) {
                hr.removeHandler();
            }
        }

        view.getEditorDriver().accept(new DeleteArgumentGroup(event.getArgumentGroupToBeDeleted(), appearance));
    }

    @Override
    public AppTemplate getAppTemplate() {
        return appTemplate != null ? flushViewAndClean() : null;
    }

    @Override
    public void go(HasOneWidget container) {
        setLabelOnlyEditMode(appTemplate.isPublic());
        /*
         * JDS Make a copy so we can check for differences on exit.
         */
        this.lastSave = AppTemplateUtils.copyAppTemplate(appTemplate);

        view.getEditorDriver().edit(appTemplate);
        view.onArgumentSelected(new ArgumentSelectedEvent(null));
        /*
         * JDS Set postEdit to true to enable handling of ArgumentGroupAddedEvents and
         * ArgumentAddedEvents
         */
        postEdit = true;
        view.getEditorDriver().accept(new InitLabelOnlyEditMode(isLabelOnlyEditMode()));

        view.getEditorDriver().accept(new InitializeDragAndDrop(this));
        GatherAllEventProviders gatherAllEventProviders = new GatherAllEventProviders(appearance, this, this);
        view.getEditorDriver().accept(gatherAllEventProviders);
        view.getEditorDriver().accept(new RegisterEventHandlers(this, this, this, gatherAllEventProviders));

        updateCommandLinePreview(lastSave);
        if (container.getWidget() == null) {
            // JDS Only set widget if container has no widget.
            container.setWidget(view);
        }
    }
    
    @Override
    public void go(final HasOneWidget container, final AppTemplate appTemplate) {
        go(container, appTemplate, null);
    }

    @Override
    public void go(final HasOneWidget container, final AppTemplate appTemplate, final RenameWindowHeaderCommand renameCmd) {
        this.renameCmd = renameCmd;
        // If we are editing a new AppTemplate, and the current the current AppTemplate has unsaved changes
        if ((appTemplate != null) && (lastSave != null) && isEditorDirty() && !Strings.nullToEmpty(appTemplate.getId()).equals(Strings.nullToEmpty(lastSave.getId()))) {

            // JDS ScheduleDeferred to ensure that the dialog's show() method is called after any parent container's show() method.
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    if (!isViewValid()) {
                        // JDS View has changes, but contains errors.
                        new ContainsErrorsOnSwitchDialog(messages, I18N.ERROR, container, appTemplate, renameCmd).show();
                    } else {
                        // JDS There are differences and form is valid, so prompt user to save.
                        new PromptForSaveThenSwitchDialog(messages, container, appTemplate, renameCmd).show();
                    }

                }
            });
        } else {
            this.appTemplate = appTemplate;
            go(container);
            if (renameCmd != null) {
                renameCmd.execute();
            }
        }
    }

    @Override
    public boolean isEditorDirty() {
        try {
            // Determine if there are any changes, variables are broken out for readability
            AutoBean<AppTemplate> lastSaveAb = AutoBeanUtils.getAutoBean(lastSave);
            AutoBean<AppTemplate> currentAb = AutoBeanUtils.getAutoBean(AppTemplateUtils.copyAppTemplate(flushViewAndClean()));
            String lastSavePayload = AutoBeanCodex.encode(lastSaveAb).getPayload();
            String currentPayload = AutoBeanCodex.encode(currentAb).getPayload();
            boolean areEqual = lastSavePayload.equals(currentPayload);
            return !areEqual;
        } catch (IllegalStateException e) {
            /*
             * JDS This is expected to occur when 'flush()' is called when 'edit()' was not called first.
             */
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isLabelOnlyEditMode() {
        return onlyLabelEditMode;
    }

    @Override
    public void onArgumentAdded(ArgumentAddedEvent event) {
        if (!postEdit) {
            return;
        }

        view.getEditorDriver().accept(new InitializeArgumentEventManagement(event.getArgumentEditor()));
    }

    @Override
    public void onArgumentGroupAdded(ArgumentGroupAddedEvent event) {
        if (!postEdit) {
            return;
        }

        AutoBean<ArgumentGroup> autoBean = AutoBeanUtils.getAutoBean(event.getArgumentGroup());
        view.getEditorDriver().accept(new InitializeArgumentGroupEventManagement(autoBean, event.getArgumentGroupEditor(), this));
        event.getArgumentGroupEditor().addArgumentAddedEventHandler(this);
        event.getArgumentGroupEditor().addArgumentGroupSelectedHandler(view);
    }

    @Override
    public void onArgumentOrderClicked() {
    
        AppTemplate flushRawApp = view.getEditorDriver().flush();
        final List<Argument> allTemplateArguments = getAllTemplateArguments(flushRawApp);
        uuidService.getUUIDs(allTemplateArguments.size(), new AsyncCallback<List<String>>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<String> result) {

                final IPlantDialog dlg = new IPlantDialog();
                dlg.setPredefinedButtons(PredefinedButton.OK);
                dlg.setHeadingText(appIntMessages.commandLineOrder());
                dlg.setModal(true);
                dlg.setOkButtonText(messages.done());
                dlg.setAutoHide(false);
                CommandLineOrderingPanel clop = new CommandLineOrderingPanel(allTemplateArguments, AppsEditorPresenterImpl.this, appIntMessages, result);
                clop.setSize("640", "480");
                dlg.add(clop);
                dlg.show();
            }
        });
    }

    @Override
    public void onBeforeHide(final BeforeHideEvent event) {
        if ((event.getSource() instanceof IsMinimizable) && ((IsMinimizable)event.getSource()).isMinimized()) {
            return;
        }
        if (isEditorDirty()) {
            event.setCancelled(true);
            final Component component = event.getSource();
            if (isViewValid()) {
                // JDS There are differences and form is valid, so prompt user to save.
                new PromptForSaveDialog(messages, component, beforeHideHandlerRegistration).show();
            } else {
                new ContainsErrorsOnHideDialog(messages, I18N.ERROR, component,
                        beforeHideHandlerRegistration).show();
            }
        }
    }

    @Override
    public void onPreviewJsonClicked() {
        AppTemplate appTemplate = flushViewAndClean();
        Splittable split = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(AppTemplateUtils.removeEmptyGroupArguments(appTemplate)));
        IPlantDialog dlg = new IPlantDialog();
        dlg.setHeadingText(appIntMessages.previewJSON());
        dlg.setPredefinedButtons(PredefinedButton.OK);
        dlg.setSize("500", "350");
        dlg.setResizable(false);
        dlg.show();
        doJsonFormattting(dlg.getBody(),JsonUtil.prettyPrint(split.getPayload(), null, 4), dlg.getBody().getOffsetWidth(),dlg.getBody().getOffsetHeight());
        dlg.forceLayout();
    }

    @Override
    public void onPreviewUiClicked() {
        AppLaunchPreviewViewImpl preview = AppsEditorInjector.INSTANCE.getAppLaunchPreviewView();
        preview.edit(flushViewAndClean(), null);
        preview.show();
    }

    @Override
    public void onSaveClicked() {
        if (isViewValid()) {
            doOnSaveClicked(null);
        } else {
            IplantInfoBox errorsInfo = new IplantInfoBox(messages.warning(),
                    I18N.ERROR.appContainsErrorsUnableToSave());
            errorsInfo.setIcon(MessageBox.ICONS.error());
            errorsInfo.show();
        }
    }

    @Override
    public void onUpdateCommandLinePreview(UpdateCommandLinePreviewEvent event) {
        updateCommandLinePreview(view.getEditorDriver().flush());
    }

    @Override
    public boolean orderingRequired(Argument arg) {
        if (arg == null) {
            return false;
        }
        ArgumentType type = arg.getType();
        if (type.equals(ArgumentType.Info) || type.equals(ArgumentType.EnvironmentVariable)) {
            return false;
        }
    
        DataObject dataObject = arg.getDataObject();
        boolean isOutput = ArgumentType.FileOutput.equals(type)
                || ArgumentType.FolderOutput.equals(type) || ArgumentType.MultiFileOutput.equals(type);
        return !(isOutput && (dataObject != null) && dataObject.isImplicit());
    }

    @Override
    public void setBeforeHideHandlerRegistration(HandlerRegistration hr) {
        this.beforeHideHandlerRegistration = hr;
    }

    @Override
    public void setLabelOnlyEditMode(boolean onlyLabelEditMode) {
        this.onlyLabelEditMode = onlyLabelEditMode;
        view.setOnlyLabelEditMode(onlyLabelEditMode);
    }

    private void doOnSaveClicked(AsyncCallback<Void> onSaveCallback) {
        AppTemplate toBeSaved = flushViewAndClean();

        // Update the AppTemplate's edited and published date.
        Date currentTime = new Date();
        toBeSaved.setEditedDate(currentTime);
        toBeSaved.setPublishedDate(currentTime);

        final List<Argument> argNeedUuid = Lists.newArrayList();
        // First loop over AppTemplate and look for UUIDs which need to be applied
        for (ArgumentGroup ag : toBeSaved.getArgumentGroups()) {
            for (Argument arg : ag.getArguments()) {
                if (Strings.isNullOrEmpty(arg.getId())) {
                    argNeedUuid.add(arg);
                }
            }
        }

        // Check if we have anything which needs a UUID
        if (argNeedUuid.size() > 0) {
            uuidService.getUUIDs(argNeedUuid.size(), new GetUuidThenDoSaveCallback(argNeedUuid, toBeSaved, onSaveCallback));
        } else {
            doSave(toBeSaved, onSaveCallback);
        }
    }

    private void doSave(AppTemplate toBeSaved, final AsyncCallback<Void> onSaveCallback) {
        // JDS Make a copy so we can check for differences on exit
        lastSave = AppTemplateUtils.copyAppTemplate(toBeSaved);

        // Remove id if it is a new app.
        if (lastSave.getId().equals("NEW_APP_TEMPLATE")) {
            lastSave.setId("");
        }
        AsyncCallback<String> saveCallback = new AsyncCallback<String>() {
    
            @Override
            public void onFailure(Throwable caught) {
                IplantAnnouncer.getInstance().schedule(new ErrorAnnouncementConfig(errorMessages.unableToSave()));
                if (onSaveCallback != null) {
                    onSaveCallback.onFailure(caught);
                }
            }
    
            @Override
            public void onSuccess(String result) {
                if (Strings.isNullOrEmpty(appTemplate.getId())) {
                    appTemplate.setId(result);
                } else if (appTemplate.getId().equalsIgnoreCase(result)) {
                    // JDS There was an app ID, but now we are changing it. This is undesired.
                    GWT.log("Attempt to change app ID from \"" + appTemplate.getId() + "\" to \"" + result + "\"");
                }
                lastSave = AppTemplateUtils.copyAppTemplate(flushViewAndClean());
                if (renameCmd != null) {
                    renameCmd.setAppTemplate(lastSave);
                    renameCmd.execute();
                }
                eventBus.fireEvent(new AppUpdatedEvent(lastSave));

                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appIntMessages.saveSuccessful()));
                if (onSaveCallback != null) {
                    onSaveCallback.onSuccess(null);
                }
            }
        };

        if (isLabelOnlyEditMode()) {
            atService.updateAppLabels(lastSave, saveCallback);
        } else {
            atService.saveAndPublishAppTemplate(lastSave, saveCallback);
        }
    
    }

    private AppTemplate flushViewAndClean() {
        return AppTemplateUtils.removeEmptyGroupArguments(view.flush());
    }

    private List<Argument> getAllTemplateArguments(AppTemplate at) {
        if (at == null) {
            return Collections.emptyList();
        }
        List<Argument> args = Lists.newArrayList();
        for (ArgumentGroup ag : at.getArgumentGroups()) {
             args.addAll(ag.getArguments());
        }
        return args;
    }

    private boolean isViewValid() {
        flushViewAndClean();
        return !view.hasErrors();
    }

    private void updateCommandLinePreview(final AppTemplate at) {
        AppTemplate cleaned = AppTemplateUtils.removeEmptyGroupArguments(at);
        atService.cmdLinePreview(cleaned, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(String result) {
                Splittable split = StringQuoter.split(result);
                String cmdLinePrev = split.get("params").asString();

                /*
                 * JDS If the given AppTemplate has a valid DeployedComponent, prepend the
                 * DeployedComponent name to the command line preview
                 */
                if ((at.getDeployedComponent() != null) && !Strings.isNullOrEmpty(at.getDeployedComponent().getName())) {
                    cmdLinePrev = at.getDeployedComponent().getName() + " " + cmdLinePrev;
                }
                view.setCmdLinePreview(cmdLinePrev);
            }
        });
    }

}
