package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.apps.client.events.AppPublishedEvent;
import org.iplantc.de.apps.client.events.AppPublishedEvent.AppPublishedEventHandler;
import org.iplantc.de.apps.integration.client.view.AppsEditorView;
import org.iplantc.de.apps.integration.shared.AppIntegrationModule;
import org.iplantc.de.apps.widgets.client.view.AppLaunchView.RenameWindowHeaderCommand;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.ToolServices;
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
import org.iplantc.de.desktop.client.events.WindowHeadingUpdatedEvent;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A window for the App Integration editor
 * <p/>
 * 
 * @author jstroot, sriram, psarando
 */
public class AppEditorWindow extends IplantWindowBase implements AppPublishedEventHandler {
    /**
     * This command is passed to the {@link AppsEditorView.Presenter} to communicate when this window's
     * title should be updated.
     * 
     * @author jstroot
     */
    private final class RenameWindowHeaderCmdImpl implements RenameWindowHeaderCommand {
        private final AppEditorWindow window;
        private AppTemplate appTemplate;

        public RenameWindowHeaderCmdImpl(AppEditorWindow window) {
            this.window = window;
        }

        @Override
        public void execute() {
            // JDS Don't update window title for new, un-saved apps.
            if (Strings.isNullOrEmpty(appTemplate.getId())) {
                return;
            }
            final String name = !Strings.isNullOrEmpty(appTemplate.getName()) ? appTemplate.getName()
                                                                             : appearance.headingText();
            LOG.log(Level.FINE, "app template " + appTemplate.isPublic());
            if (appTemplate.isPublic() != null && appTemplate.isPublic() == true) {
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

    public interface AppEditorAppearance {

        String appDefaultName();

        String appPublishedError();

        SafeHtml editPublicAppWarningTitle(SafeHtml appName);

        String groupDefaultLabel(int i);

        String headingText();

        String loadingMask();

        int minHeight();

        int minWidth();

        String unableToRetrieveWorkflowGuide();
    }

    final AppEditorAppearance appearance;
    private final IplantAnnouncer announcer;
    private final AppTemplateUtils appTemplateUtils;
    private final ToolServices dcServices;
    private final AppTemplateAutoBeanFactory factory;
    private final EventBus eventBus;
    private final AppsEditorView.Presenter presenter;
    private final RenameWindowHeaderCmdImpl renameCmd;
    private final AppTemplateServices templateService;
    private final ContextualHelpToolButton editPublicAppContextHlpTool;
    Logger LOG = Logger.getLogger("App Editor window");

    @Inject
    AppEditorWindow(final AppEditorAppearance appearance,
                    final AppsEditorView.Presenter presenter,
                    final AppTemplateUtils appTemplateUtils,
                    final IplantAnnouncer announcer,
                    final AppTemplateServices templateService,
                    final ToolServices dcServices,
                    final AppTemplateAutoBeanFactory factory,
                    final AppsWidgetsContextualHelpMessages helpMessages,
                    final EventBus eventBus) {
        this.appearance = appearance;
        this.appTemplateUtils = appTemplateUtils;
        this.presenter = presenter;
        this.announcer = announcer;
        this.templateService = templateService;
        this.dcServices = dcServices;
        this.factory = factory;
        this.eventBus = eventBus;

        editPublicAppContextHlpTool = new ContextualHelpToolButton(new HTML(helpMessages.editPublicAppHelp()));
        renameCmd = new RenameWindowHeaderCmdImpl(this);

        setHeadingText(appearance.headingText());
        setSize("800", "480");
        setMinWidth(appearance.minWidth());
        setMinHeight(appearance.minHeight());
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(getUpdatedConfig());
    }

    @Override
    public void onAppPublished(AppPublishedEvent appPublishedEvent) {
        App publishedApp = appPublishedEvent.getPublishedApp();
        AppTemplate currentAt = presenter.getAppTemplate();
        // JDS If the published App is the current edited AppTemplate, refetch app Template
        if (currentAt != null && publishedApp.getId().equalsIgnoreCase(currentAt.getId())) {

            if (presenter.isEditorDirty()) {
                // JDS If the editor has unsaved changes, inform user that they will be thrown away.
                announcer.schedule(new ErrorAnnouncementConfig(appearance.appPublishedError()));
            }
            AppsIntegrationWindowConfig appIntConfig = ConfigFactory.appsIntegrationWindowConfig(publishedApp.getId());
            appIntConfig.setOnlyLabelEditMode(true);
            update(appIntConfig);
        }
    }

    @Override
    public <C extends WindowConfig> void show(C windowConfig, String tag, boolean isMaximizable) {

        // JDS Add presenter as a before hide handler to determine if user has changes before closing.
        presenter.setBeforeHideHandlerRegistration(this.addBeforeHideHandler(presenter));
        eventBus.addHandler(AppPublishedEvent.TYPE, this);
        init(presenter, (AppsIntegrationWindowConfig)windowConfig);
        super.show(windowConfig, tag, isMaximizable);

        ensureDebugId(DeModule.WindowIds.APP_EDITOR_WINDOW);
    }

    @Override
    public <C extends WindowConfig> void update(final C config) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {

            }

            @Override
            public void onSuccess() {

                AppsIntegrationWindowConfig appIntConfig = (AppsIntegrationWindowConfig)config;
                init(presenter, appIntConfig);
            }
        });
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        presenter.setViewDebugId(baseID + AppIntegrationModule.Ids.APP_EDITOR_VIEW);
    }

    @Override
    protected void onShow() {
        setMaximized(true);
    }

    private AppsIntegrationWindowConfig getUpdatedConfig() {
        AppTemplate appTemplate = presenter.getAppTemplate();
        AppsIntegrationWindowConfig config = ConfigFactory.appsIntegrationWindowConfig(appTemplate == null ? ""
                                                                                                          : Strings.nullToEmpty(appTemplate.getId()));
        config.setAppTemplate(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(appTemplate)));
        return config;
    }

    private void
            init(final AppsEditorView.Presenter presenter, final AppsIntegrationWindowConfig config) {
        if (config.getAppTemplate() != null) {
            // JDS Use converter for convenience.
            AppTemplateCallbackConverter at = new AppTemplateCallbackConverter(factory,
                                                                               new AsyncCallback<AppTemplate>() {

                                                                                   @Override
                                                                                   public void
                                                                                           onFailure(Throwable caught) {
                                                                                       /*
                                                                                        * JDS Do nothing
                                                                                        * since this this
                                                                                        * callback
                                                                                        * converter is
                                                                                        * called manually
                                                                                        * below (i.e. no
                                                                                        * over-the-wire
                                                                                        * integration)
                                                                                        */
                                                                                   }

                                                                                   @Override
                                                                                   public void
                                                                                           onSuccess(AppTemplate result) {
                                                                                       // KLUDGE until
                                                                                       // service returns
                                                                                       // this value in
                                                                                       // JSON response.
                                                                                       if (result.isPublic() != null) {
                                                                                           result.setPublic(result.isPublic());
                                                                                       } else if (config != null) {
                                                                                           result.setPublic(config.isOnlyLabelEditMode());
                                                                                       }
                                                                                       renameCmd.setAppTemplate(result);
                                                                                       presenter.go(AppEditorWindow.this,
                                                                                                    result,
                                                                                                    renameCmd);
                                                                                       AppEditorWindow.this.forceLayout();
                                                                                       AppEditorWindow.this.center();
                                                                                   }
                                                                               });
            at.onSuccess(config.getAppTemplate().getPayload());
        } else if (Strings.isNullOrEmpty(config.getAppId())) {
            setHeadingText(appearance.headingText());

            // Create empty AppTemplate
            AppTemplate newAppTemplate = factory.appTemplate().as();
            newAppTemplate.setName(appearance.appDefaultName());
            newAppTemplate.setPublic(false);
            ArgumentGroup argGrp = factory.argumentGroup().as();
            argGrp.setName("");
            argGrp.setLabel(appearance.groupDefaultLabel(1));
            argGrp.setArguments(Lists.<Argument> newArrayList());
            newAppTemplate.setArgumentGroups(Lists.newArrayList(argGrp));
            newAppTemplate.setId(null);

            /*
             * JDS Set the id of the AppTemplate passed to the rename command to newAppTemplate. This is
             * to ensure that the window title is not changed until a new app has been saved.
             */
            final AppTemplate copyAppTemplate = appTemplateUtils.copyAppTemplate(newAppTemplate);
            renameCmd.setAppTemplate(copyAppTemplate);

            presenter.go(this, newAppTemplate, renameCmd);
            AppEditorWindow.this.forceLayout();
        } else {
            mask(appearance.loadingMask());
            templateService.getAppTemplateForEdit(CommonModelUtils.getInstance()
                                                                  .createHasIdFromString(config.getAppId()),
                                                  new AsyncCallback<AppTemplate>() {
                                                      @Override
                                                      public void onFailure(Throwable caught) {
                                                          SimpleServiceError serviceError = AutoBeanCodex.decode(factory,
                                                                                                                 SimpleServiceError.class,
                                                                                                                 caught.getMessage())
                                                                                                         .as();
                                                          announcer.schedule(new ErrorAnnouncementConfig(appearance.unableToRetrieveWorkflowGuide()
                                                                  + ": " + serviceError.getReason()));
                                                          ErrorHandler.post(appearance.unableToRetrieveWorkflowGuide(),
                                                                            caught);
                                                          AppEditorWindow.this.hide();
                                                      }

                                                      @Override
                                                      public void onSuccess(AppTemplate result) {
                                                          // KLUDGE until service returns this value in
                                                          // JSON response.
                                                          if (result.isPublic() != null) {
                                                              result.setPublic(result.isPublic());
                                                          } else if (config != null) {
                                                              result.setPublic(config.isOnlyLabelEditMode());
                                                          }
                                                          renameCmd.setAppTemplate(result);
                                                          presenter.go(AppEditorWindow.this,
                                                                       result,
                                                                       renameCmd);
                                                          AppEditorWindow.this.unmask();
                                                          AppEditorWindow.this.forceLayout();
                                                          AppEditorWindow.this.center();
                                                      }
                                                  });
        }
    }

    private void setEditPublicAppHeader(String appName) {
        setHeadingHtml(appearance.editPublicAppWarningTitle(SafeHtmlUtils.fromString(appName)));

        // JDS Only insert if not there.
        if (getHeader().getTool(0) != editPublicAppContextHlpTool) {
            getHeader().insertTool(editPublicAppContextHlpTool, 0);
        }
    }
}
