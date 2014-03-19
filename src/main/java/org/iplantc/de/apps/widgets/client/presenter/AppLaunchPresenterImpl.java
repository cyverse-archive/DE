package org.iplantc.de.apps.widgets.client.presenter;

import org.iplantc.de.apps.widgets.client.events.AnalysisLaunchEvent;
import org.iplantc.de.apps.widgets.client.events.AnalysisLaunchEvent.AnalysisLaunchEventHandler;
import org.iplantc.de.apps.widgets.client.events.RequestAnalysisLaunchEvent.RequestAnalysisLaunchEventHandler;
import org.iplantc.de.apps.widgets.client.view.AppLaunchView;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.JobExecution;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.util.RegExp;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsDisplayMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsErrorMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.util.Format;

/**
 * 
 * @author jstroot
 *
 */
public class AppLaunchPresenterImpl implements AppLaunchView.Presenter, RequestAnalysisLaunchEventHandler {

    @Inject
    private AppsWidgetsDisplayMessages appsWidgetsDisplayMessages;
    @Inject
    private AppsWidgetsErrorMessages appsWidgetsErrMessages;
    private AppTemplate appTemplate;
    private final AppTemplateServices atServices;
    private HandlerManager handlerManager;
    private final UserInfo userInfo;
    private final UserSettings userSettings;

    @Inject
    private IplantValidationConstants valConstants;
    private final AppLaunchView view;

    @Inject
    public AppLaunchPresenterImpl(final AppLaunchView view, final UserSettings userSettings, final UserInfo userInfo, final AppTemplateServices atServices) {
        this.view = view;
        this.userSettings = userSettings;
        this.userInfo = userInfo;
        this.view.addRequestAnalysisLaunchEventHandler(this);
        this.atServices = atServices;
    }
    
    @Override
    public void addAnalysisLaunchHandler(AnalysisLaunchEventHandler handler) {
        ensureHandlers().addHandler(AnalysisLaunchEvent.TYPE, handler);
    }

    @Override
    public AppTemplate getAppTemplate() {
        return appTemplate;
    }

    @Override
    public void go(final HasOneWidget container) {

        final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
        final JobExecution je = factory.jobExecution().as();
        je.setAppTemplateId(appTemplate.getId());
        je.setEmailNotificationEnabled(userSettings.isEnableEmailNotification());
        je.setWorkspaceId(userInfo.getWorkspaceId());
        // JDS Replace all Cmd Line restricted chars with underscores
        String regex = Format.substitute("[{0}]", RegExp.escapeCharacterClassSet(valConstants.restrictedCmdLineChars()));
        String newName = appTemplate.getName().replaceAll(regex, "_");
        je.setName(newName + "_" + appsWidgetsDisplayMessages.defaultAnalysisName()); //$NON-NLS-1$
        je.setOutputDirectory(userSettings.getDefaultOutputFolder().getPath());

        view.edit(appTemplate, je);
        container.setWidget(view);
    }

    @Override
    public void go(HasOneWidget container, AppTemplate appTemplate) {
        this.appTemplate = appTemplate;
        go(container);
    }

    @Override
    public void onAnalysisLaunchRequest(final AppTemplate at, final JobExecution je) {
        launchAnalysis(at, je);
    }

    HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
    }

    private HandlerManager createHandlerManager() {
        return new HandlerManager(this);
    }

    private void launchAnalysis(final AppTemplate at, final JobExecution je) {
        atServices.launchAnalysis(at, je, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                IplantAnnouncer.getInstance().schedule(new ErrorAnnouncementConfig(appsWidgetsErrMessages.launchAnalysisFailure(je.getName())));
                ErrorHandler.post(I18N.ERROR.analysisFailedToLaunch(at.getName()), caught);
                view.analysisLaunchFailed();
            }

            @Override
            public void onSuccess(String result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appsWidgetsDisplayMessages.launchAnalysisSuccess(je.getName())));
                ensureHandlers().fireEvent(new AnalysisLaunchEvent(at));
            }
        });
    }
}
