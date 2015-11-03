package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.apps.widgets.client.events.AnalysisLaunchEvent;
import org.iplantc.de.apps.widgets.client.events.AnalysisLaunchEvent.AnalysisLaunchEventHandler;
import org.iplantc.de.apps.widgets.client.view.AppLaunchView;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.converters.AppTemplateCallbackConverter;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.views.window.configs.AppWizardConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.client.events.WindowHeadingUpdatedEvent;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * @author jstroot
 */
public class AppLaunchWindow extends IplantWindowBase implements AnalysisLaunchEventHandler {

    private final class AppTemplateCallback implements AsyncCallback<AppTemplate> {
        private final IplantErrorStrings errorStrings;
        private final AppLaunchView.Presenter presenter1;

        private AppTemplateCallback(AppLaunchView.Presenter presenter,
                                    final IplantErrorStrings errorStrings) {
            this.presenter1 = presenter;
            this.errorStrings = errorStrings;
        }

        @Override
        public void onFailure(Throwable caught) {
            AppLaunchWindow.this.clear();
            ErrorHandler.post(errorStrings.unableToRetrieveWorkflowGuide(), caught);
        }

        @Override
        public void onSuccess(AppTemplate result) {
            if (result.isAppDisabled()) {
                ErrorAnnouncementConfig config = new ErrorAnnouncementConfig(displayStrings.appUnavailable());
                IplantAnnouncer.getInstance().schedule(config);
                AppLaunchWindow.this.hide();
                return;
            }
            AppLaunchWindow.this.clear();
            presenter1.go(AppLaunchWindow.this, result);
            AppLaunchWindow.this.setHeadingText(presenter1.getAppTemplate().getLabel());
            AppLaunchWindow.this.fireEvent(new WindowHeadingUpdatedEvent());
            // KLUDGE JDS This call to forceLayout should not be necessary.
            AppLaunchWindow.this.forceLayout();
        }
    }
    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private final AppTemplateAutoBeanFactory factory;
    private final AppLaunchView.Presenter presenter;
    private final AppTemplateServices templateService;
    private String appId;

    @Inject
    AppLaunchWindow(final AppLaunchView.Presenter presenter,
                    final AppTemplateServices templateService,
                    final AppTemplateAutoBeanFactory factory,
                    final IplantDisplayStrings displayStrings,
                    final IplantErrorStrings errorStrings) {
        this.presenter = presenter;
        this.templateService = templateService;
        this.factory = factory;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;

        setSize("640", "375");
        setMinWidth(300);
        setMinHeight(350);
        setBorders(false);
        ensureDebugId(DeModule.WindowIds.APP_LAUNCH_WINDOW);

        presenter.addAnalysisLaunchHandler(this);
    }

    @Override
    public WindowState getWindowState() {
        AppWizardConfig config = ConfigFactory.appWizardConfig(appId);
        config.setAppTemplate(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(presenter.getAppTemplate())));
        return createWindowState(config);
    }

    @Override
    public void onAnalysisLaunch(AnalysisLaunchEvent analysisLaunchEvent) {
        if (analysisLaunchEvent.getAppTemplateId().getId().equalsIgnoreCase(appId)) {
            hide();
        }
    }

    @Override
    public <C extends WindowConfig> void show(final C windowConfig,
                                              final String tag,
                                              final boolean isMaximizable) {
        final AppWizardConfig config1 = (AppWizardConfig) windowConfig;
        appId = config1.getAppId();
        init(presenter, config1);
        super.show(windowConfig, tag, isMaximizable);
    }

    private void init(final AppLaunchView.Presenter presenter, AppWizardConfig config) {
        SimpleContainer sc = new SimpleContainer();
        this.setWidget(sc);

        sc.mask(displayStrings.loadingMask());
        if (config.getAppTemplate() != null) {
            AppTemplateCallbackConverter cnvt = new AppTemplateCallbackConverter(factory,
                                                                                 new AsyncCallback<AppTemplate>() {

                                                                                     @Override
                                                                                     public void onFailure(Throwable caught) {
                            /*
                             * JDS Do nothing since this this callback converter is called manually below
                             * (i.e. no over-the-wire integration)
                             */
                                                                                     }

                                                                                     @Override
                                                                                     public void onSuccess(AppTemplate result) {
                                                                                         setHeadingText(result.getLabel());
                                                                                         AppLaunchWindow.this.clear();
                                                                                         presenter.go(AppLaunchWindow.this, result);
                                                                                     }
                                                                                 });
            cnvt.onSuccess(config.getAppTemplate().getPayload());

            // KLUDGE JDS This call to forceLayout should not be necessary.
            forceLayout();
        } else if (config.isRelaunchAnalysis()) {
            templateService.rerunAnalysis(config.getAnalysisId(),
                                          config.getAppId(),
                                          new AppTemplateCallback(presenter, errorStrings));
        } else {
            templateService.getAppTemplate(CommonModelUtils.getInstance().createHasIdFromString(config.getAppId()),
                                           new AppTemplateCallback(presenter, errorStrings));
        }
    }

}
