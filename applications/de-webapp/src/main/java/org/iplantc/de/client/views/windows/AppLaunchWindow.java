package org.iplantc.de.client.views.windows;

import org.iplantc.de.apps.widgets.client.events.AnalysisLaunchEvent;
import org.iplantc.de.apps.widgets.client.events.AnalysisLaunchEvent.AnalysisLaunchEventHandler;
import org.iplantc.de.apps.widgets.client.gin.AppLaunchInjector;
import org.iplantc.de.apps.widgets.client.view.AppLaunchView;
import org.iplantc.de.client.events.WindowHeadingUpdatedEvent;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.DeployedComponentServices;
import org.iplantc.de.client.services.converters.AppTemplateCallbackConverter;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.views.window.configs.AppWizardConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class AppLaunchWindow extends IplantWindowBase implements AnalysisLaunchEventHandler {

    private final class AppTemplateCallback implements AsyncCallback<AppTemplate> {
        private final AppLaunchView.Presenter presenter1;

        private AppTemplateCallback(AppLaunchView.Presenter presenter) {
            this.presenter1 = presenter;
        }

        @Override
        public void onSuccess(AppTemplate result) {
            if (result.isAppDisabled()) {
                ErrorAnnouncementConfig config = new ErrorAnnouncementConfig(
                        org.iplantc.de.resources.client.messages.I18N.DISPLAY.appUnavailable());
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

        @Override
        public void onFailure(Throwable caught) {
            AppLaunchWindow.this.clear();
            ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.unableToRetrieveWorkflowGuide(), caught);
        }
    }

    private final AppLaunchView.Presenter presenter;
    private final AppTemplateServices templateService = ServicesInjector.INSTANCE.getAppTemplateServices();
    private final String appId;
    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
    private final DeployedComponentServices dcServices = ServicesInjector.INSTANCE.getDeployedComponentServices();

    public AppLaunchWindow(AppWizardConfig config) {
        super(null, null);
        setSize("640", "375");
        setMinWidth(300);
        setMinHeight(350);
        setBorders(false);

        presenter = AppLaunchInjector.INSTANCE.getAppLaunchPresenter();
        presenter.addAnalysisLaunchHandler(this);
        appId = config.getAppId();
        init(presenter, config);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
    }

    private void init(final AppLaunchView.Presenter presenter, AppWizardConfig config) {
        SimpleContainer sc = new SimpleContainer();
        this.setWidget(sc);

        sc.mask(org.iplantc.de.resources.client.messages.I18N.DISPLAY.loadingMask());
        if (config.getAppTemplate() != null) {
            AppTemplateCallbackConverter cnvt = new AppTemplateCallbackConverter(factory, dcServices,
                    new AsyncCallback<AppTemplate>() {

                        @Override
                        public void onSuccess(AppTemplate result) {
                            setHeadingText(result.getLabel());
                            AppLaunchWindow.this.clear();
                            presenter.go(AppLaunchWindow.this, result);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            /*
                             * JDS Do nothing since this this callback converter is called manually below
                             * (i.e. no over-the-wire integration)
                             */
                        }
                    });
            cnvt.onSuccess(config.getAppTemplate().getPayload());

            // KLUDGE JDS This call to forceLayout should not be necessary.
            forceLayout();
        } else if (config.isRelaunchAnalysis()) {
            templateService.rerunAnalysis(config.getAnalysisId(), new AppTemplateCallback(presenter));
        } else {
            templateService.getAppTemplate(CommonModelUtils.createHasIdFromString(config.getAppId()),
                    new AppTemplateCallback(presenter));
        }
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

}
