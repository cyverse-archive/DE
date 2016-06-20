package org.iplantc.de.desktop.client.gin;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.shared.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.services.*;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.desktop.client.presenter.DesktopPresenterEventHandler;
import org.iplantc.de.desktop.client.presenter.DesktopPresenterImpl;
import org.iplantc.de.desktop.client.presenter.DesktopPresenterWindowEventHandler;
import org.iplantc.de.desktop.client.presenter.DesktopWindowManager;
import org.iplantc.de.desktop.client.presenter.util.MessagePoller;
import org.iplantc.de.desktop.client.views.DesktopViewImpl;
import org.iplantc.de.desktop.client.views.widgets.DEFeedbackDialog;
import org.iplantc.de.desktop.client.views.widgets.PreferencesDialog;
import org.iplantc.de.desktop.client.views.windows.*;
import org.iplantc.de.desktop.client.views.windows.util.WindowFactory;
import org.iplantc.de.notifications.client.utils.NotifyInfo;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.systemMessages.client.presenter.NewMessagePresenter;
import org.iplantc.de.systemMessages.client.view.NewMessageView;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.sencha.gxt.widget.core.client.WindowManager;

/**
 * @author jstroot
 */
public class DEGinModule extends AbstractGinModule {

    //<editor-fold desc="Services">
    @Provides public AnalysisServiceFacade createAnalysisService() {
        return ServicesInjector.INSTANCE.getAnalysisServiceFacade();
    }

    @Provides @Singleton public DiskResourceServiceFacade createDiskResourceService() {
        return ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
    }

    @Provides public UserSessionServiceFacade createUserSessionServiceFacade() {
        return ServicesInjector.INSTANCE.getUserSessionServiceFacade();
    }

    @Provides public FileEditorServiceFacade createFileEditorService() {
        return ServicesInjector.INSTANCE.getFileEditorServiceFacade();
    }

    @Provides public DEFeedbackServiceFacade createFeedbackService() {
        return ServicesInjector.INSTANCE.getDeFeedbackServiceFacade();
    }

    @Provides public MessageServiceFacade createMessageServiceFacade () {
        return ServicesInjector.INSTANCE.getMessageServiceFacade();
    }

    @Provides @Singleton public AppTemplateServices createAppTemplateServices() {
        return ServicesInjector.INSTANCE.getAppTemplateServices();
    }

    @Provides
    @Singleton
    public AppBuilderMetadataServiceFacade createAppBuilderMetadataServiceFacade() {
        return ServicesInjector.INSTANCE.getAppMetadataService();
    }

    @Provides @Singleton public AppUserServiceFacade createAppUserServiceFacade(){
        return ServicesInjector.INSTANCE.getAppUserServiceFacade();
    }

    @Provides @Singleton public AppServiceFacade createAppServiceFacade() {
        return ServicesInjector.INSTANCE.getAppServiceFacade();
    }

    @Provides @Singleton public SearchServiceFacade createSearchServiceFacade() {
        return ServicesInjector.INSTANCE.getSearchServiceFacade();
    }

    @Provides public TagsServiceFacade createMetadataServiceFacade() {
        return ServicesInjector.INSTANCE.getMetadataService();
    }

    @Provides
    public FileSystemMetadataServiceFacade createFileSystemMetadataServiceFacade() {
        return ServicesInjector.INSTANCE.getFileSysteMetadataServiceFacade();
    }

    @Provides
    public ToolServices createToolsServices() {
        return ServicesInjector.INSTANCE.getDeployedComponentServices();
    }

    //</editor-fold>

    @Provides @Singleton public NotifyInfo createNotifyInfo() {
        return NotifyInfo.getInstance();
    }

    @Provides @Singleton public EventBus createGlobalEventBus() {
        return EventBus.getInstance();
    }

    @Provides public IplantResources createIplantResources() {
        return IplantResources.RESOURCES;
    }

    @Provides @Singleton public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

    @Provides @Singleton public UserSettings createUserSettings() {
        return UserSettings.getInstance();
    }

    @Provides @Singleton public DEProperties getDeProperties() {
        return DEProperties.getInstance();
    }

    @Provides @Singleton public MessagePoller createMessagePoller() {
        return MessagePoller.getInstance();
    }

    @Provides @Singleton public WindowManager getWindowManager() {
        return WindowManager.get();
    }

    @Provides @Singleton public IplantAnnouncer createAnnouncer() {
        return IplantAnnouncer.getInstance();
    }


    @Override
    protected void configure() {
        bind(DesktopView.class).to(DesktopViewImpl.class);
        bind(DesktopView.Presenter.class).to(DesktopPresenterImpl.class);

        bind(NewMessageView.Presenter.class).to(NewMessagePresenter.class);
        bind(DesktopPresenterEventHandler.class);
        bind(DesktopPresenterWindowEventHandler.class);
        bind(DesktopWindowManager.class).in(Singleton.class);

        bind(WindowFactory.class);
        bind(PreferencesDialog.class);
        bind(DEFeedbackDialog.class);


        // KLUDGE Bind AppsView here to get around Gin double-binding with Belphegor
//        bind(AppsView.class).to(AppsViewImpl.class);

        // Bind Windows
        bind(AboutApplicationWindow.class);
        bind(AppEditorWindow.class);
        bind(AppLaunchWindow.class);
        bind(DEAppsWindow.class);
        bind(DeDiskResourceWindow.class);
        bind(FileViewerWindow.class);
        bind(MyAnalysesWindow.class);
        bind(NotificationWindow.class);
        bind(PipelineEditorWindow.class);
        bind(SimpleDownloadWindow.class);
        bind(SystemMessagesWindow.class);
    }
}
