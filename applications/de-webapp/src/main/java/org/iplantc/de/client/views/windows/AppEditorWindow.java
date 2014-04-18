package org.iplantc.de.client.views.windows;

import static org.iplantc.de.client.models.apps.App.NEW_APP_ID;

import org.iplantc.de.apps.client.events.AppPublishedEvent;
import org.iplantc.de.apps.client.events.AppPublishedEvent.AppPublishedEventHandler;
import org.iplantc.de.apps.integration.client.gin.AppsEditorInjector;
import org.iplantc.de.apps.integration.client.view.AppsEditorView;
import org.iplantc.de.apps.widgets.client.view.AppLaunchView.RenameWindowHeaderCommand;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowHeadingUpdatedEvent;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.DeployedComponentServices;
import org.iplantc.de.client.services.converters.AppTemplateCallbackConverter;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.views.window.configs.AppsIntegrationWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.commons.client.widgets.ContextualHelpToolButton;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;
import com.sencha.gxt.widget.core.client.event.RestoreEvent.RestoreHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

import java.util.List;

/**
 * A window for the App Integration editor 
 * 
 * XXX JDS Much of the Apps Integration module config, presenter, and window closely mimic that of the App Wizard.
 * 
 * @author jstroot, sriram, psarando
 * 
 */
public class AppEditorWindow extends IplantWindowBase implements AppPublishedEventHandler {
    interface PublicAppTitleTemplate extends SafeHtmlTemplates {
        @Template("<div>"
                + "<span class='{3}'>{2}</span>" 
                + "<span class='{1}'>{0}</span>"
                + "</div>")
        SafeHtml editPublicAppWarningTitle(SafeHtml title, String titleStyle, String warningText, String warningStyle);
    }

    interface TitleStyles extends CssResource{
        String warning();
        
        String title();
    }
    
    interface Resources extends ClientBundle{
        @Source("AppIntegrationWindowTitleStyles.css")
        TitleStyles titleStyles();
    }

    private final static PublicAppTitleTemplate templates = GWT.create(PublicAppTitleTemplate.class);

    private final AppsEditorView.Presenter presenter;
    protected List<HandlerRegistration> handlers;
    private final AppTemplateServices templateService;
    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
    private final DeployedComponentServices dcServices = ServicesInjector.INSTANCE.getDeployedComponentServices();
    private final AppsWidgetsPropertyPanelLabels labels = org.iplantc.de.resources.client.messages.I18N.APPS_LABELS;
    private final RenameWindowHeaderCmdImpl renameCmd;

    final ContextualHelpToolButton editPublicAppContextHlpTool = new ContextualHelpToolButton(new HTML(org.iplantc.de.resources.client.messages.I18N.APPS_HELP.editPublicAppHelp()));
    final Resources res = GWT.create(Resources.class);

    public AppEditorWindow(AppsIntegrationWindowConfig config, final EventBus eventBus) {
        super(null, config);
        res.titleStyles().ensureInjected();

//        AppsEditorView view = new AppsEditorViewImpl(uuidService, appMetadataService);
        templateService = ServicesInjector.INSTANCE.getAppTemplateServices();
//        presenter = new AppsEditorPresenterImpl(view, eventBus, templateService, I18N.ERROR,
//                I18N.DISPLAY, uuidService);
        presenter = AppsEditorInjector.INSTANCE.getAppEditorPresenter();
        setTitle(org.iplantc.de.resources.client.messages.I18N.DISPLAY.createApps());
        setSize("800", "480");
        setMinWidth(725);
        setMinHeight(375);

        final WindowHandler windowHandler = new WindowHandler();
        addRestoreHandler(windowHandler);
        addMaximizeHandler(windowHandler);
        addShowHandler(windowHandler);

        renameCmd = new RenameWindowHeaderCmdImpl(this);
        init(presenter, config);

        // JDS Add presenter as a before hide handler to determine if user has changes before closing.
        HandlerRegistration hr = this.addBeforeHideHandler(presenter);
        presenter.setBeforeHideHandlerRegistration(hr);
        eventBus.addHandler(AppPublishedEvent.TYPE, this);
    }

    private void init(final AppsEditorView.Presenter presenter,
            final AppsIntegrationWindowConfig config) {
        if (config.getAppTemplate() != null) {
            // JDS Use converter for convenience.
            AppTemplateCallbackConverter at = new AppTemplateCallbackConverter(factory, dcServices,
                    new AsyncCallback<AppTemplate>() {

                        @Override
                        public void onSuccess(AppTemplate result) {
                            // KLUDGE until service returns this value in JSON response.
                            result.setPublic(result.isPublic() || config.isOnlyLabelEditMode());
                            renameCmd.setAppTemplate(result);
                            presenter.go(AppEditorWindow.this, result, renameCmd);
                            AppEditorWindow.this.forceLayout();
                            AppEditorWindow.this.center();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            /*
                             * JDS Do nothing since this this callback converter is called manually below
                             * (i.e. no over-the-wire integration)
                             */
                        }
                    });
            at.onSuccess(config.getAppTemplate().getPayload());
        } else if (Strings.isNullOrEmpty(config.getAppId()) || config.getAppId().equalsIgnoreCase(NEW_APP_ID)) {
            setTitle(org.iplantc.de.resources.client.messages.I18N.DISPLAY.createApps());
            // Create empty AppTemplate
            AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);

            AppTemplate newAppTemplate = factory.appTemplate().as();
            newAppTemplate.setName(labels.appDefaultName());
            ArgumentGroup argGrp = factory.argumentGroup().as();
            argGrp.setName("");
            argGrp.setLabel(labels.groupDefaultLabel(1));
            argGrp.setArguments(Lists.<Argument> newArrayList());
            newAppTemplate.setArgumentGroups(Lists.<ArgumentGroup> newArrayList(argGrp));
            newAppTemplate.setId(NEW_APP_ID);

            /*
             * JDS Set the id of the AppTemplate passed to the rename command to newAppTemplate. This is
             * to ensure that the window title is not changed until a new app has been saved.
             */
            final AppTemplate copyAppTemplate = AppTemplateUtils.copyAppTemplate(newAppTemplate);
            renameCmd.setAppTemplate(copyAppTemplate);

            presenter.go(this, newAppTemplate, renameCmd);
            AppEditorWindow.this.forceLayout();
        } else {
            mask(org.iplantc.de.resources.client.messages.I18N.DISPLAY.loadingMask());
            templateService.getAppTemplateForEdit(
                    CommonModelUtils.createHasIdFromString(config.getAppId()),
                    new AsyncCallback<AppTemplate>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            SimpleServiceError serviceError = AutoBeanCodex.decode(factory,
                                    SimpleServiceError.class, caught.getMessage()).as();
                            IplantAnnouncer.getInstance().schedule(
                                    new ErrorAnnouncementConfig(org.iplantc.de.resources.client.messages.I18N.ERROR
                                            .unableToRetrieveWorkflowGuide()
                                            + ": "
                                            + serviceError.getReason()));
                            ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.unableToRetrieveWorkflowGuide(), caught);
                            AppEditorWindow.this.hide();
                        }

                        @Override
                        public void onSuccess(AppTemplate result) {
                            // KLUDGE until service returns this value in JSON response.
                            result.setPublic(result.isPublic() || config.isOnlyLabelEditMode());
                            renameCmd.setAppTemplate(result);
                            presenter.go(AppEditorWindow.this, result, renameCmd);
                            AppEditorWindow.this.unmask();
                            AppEditorWindow.this.forceLayout();
                            AppEditorWindow.this.center();
                        }
                    });
        }
    }


    private void setEditPublicAppHeader(String appName) {
        setHeadingHtml(templates.editPublicAppWarningTitle(SafeHtmlUtils.fromString(appName), res.titleStyles().title(), 
                org.iplantc.de.resources.client.messages.I18N.APPS_MESSAGES.editPublicAppWarning(), res.titleStyles().warning()));

        // JDS Only insert if not there.
        if (getHeader().getTool(0) != editPublicAppContextHlpTool) {
            getHeader().insertTool(editPublicAppContextHlpTool, 0);
        }
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(getUpdatedConfig());
    }

    private AppsIntegrationWindowConfig getUpdatedConfig() {
        AppTemplate appTemplate = presenter.getAppTemplate();
        AppsIntegrationWindowConfig config = ConfigFactory.appsIntegrationWindowConfig(appTemplate == null ? "" : Strings.nullToEmpty(appTemplate.getId()));
        config.setAppTemplate(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(appTemplate)));
        return config;
    }

    @Override
    public <C extends WindowConfig> void update(C config) {
        AppsIntegrationWindowConfig appIntConfig = (AppsIntegrationWindowConfig)config;
        init(presenter, appIntConfig);
    }

    @Override
    public void onAppPublished(AppPublishedEvent appPublishedEvent) {
        App publishedApp = appPublishedEvent.getPublishedApp();
        AppTemplate currentAt = presenter.getAppTemplate();
        // JDS If the published App is the current edited AppTemplate, refetch app Template
        if (publishedApp.getId().equalsIgnoreCase(currentAt.getId())) {
    
            if (presenter.isEditorDirty()) {
                // JDS If the editor has unsaved changes, inform user that they will be thrown away.
                IplantAnnouncer.getInstance().schedule(new ErrorAnnouncementConfig("This app has been published before the current changes were saved. All unsaved changes have been discarded."));
            }
            AppsIntegrationWindowConfig appIntConfig = ConfigFactory.appsIntegrationWindowConfig(publishedApp.getId());
            appIntConfig.setOnlyLabelEditMode(true);
            update(appIntConfig);
        }
    
    }

    /**
     * This handler is used to manage window events.
     * 
     * @author jstroot
     * 
     */
    private final class WindowHandler implements RestoreHandler, MaximizeHandler, ShowHandler {
        @Override
        public void onRestore(RestoreEvent event) {
            AppEditorWindow.this.maximized = false;
        }
    
        @Override
        public void onMaximize(MaximizeEvent event) {
            AppEditorWindow.this.maximized = true;
        }
    
        @Override
        public void onShow(ShowEvent event) {
            AppEditorWindow.this.maximize();
        }
    }

    /**
     * This command is passed to the {@link AppsEditorView.Presenter} to communicate when this
     * window's title should be updated.
     * 
     * @author jstroot
     * 
     */
    private final class RenameWindowHeaderCmdImpl implements RenameWindowHeaderCommand {
        private AppTemplate appTemplate;
        private final AppEditorWindow window;
    
        public RenameWindowHeaderCmdImpl(AppEditorWindow window) {
            this.window = window;
        }
    
        @Override
        public void execute() {
            // JDS Don't update window title for new, un-saved apps.
            if (NEW_APP_ID.equalsIgnoreCase(appTemplate.getId())) {
                return;
            }
            final String name = !Strings.isNullOrEmpty(appTemplate.getName()) ? appTemplate.getName() : org.iplantc.de.resources.client.messages.I18N.DISPLAY.createApps();
            if (appTemplate.isPublic()) {
                window.setEditPublicAppHeader(name);
            } else {
                window.getHeader().removeTool(window.editPublicAppContextHlpTool);
                window.setHeadingText(name);
            }
            window.fireEvent(new WindowHeadingUpdatedEvent(name));
    
        }
    
        @Override
        public void setAppTemplate(AppTemplate appTemplate) {
            this.appTemplate = appTemplate;
        }
    
    }
}
