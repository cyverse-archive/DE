package org.iplantc.de.client.gin;

import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.AppsViewImpl;
import org.iplantc.de.client.desktop.DesktopView;
import org.iplantc.de.client.desktop.presenter.DesktopPresenterEventHandler;
import org.iplantc.de.client.desktop.presenter.DesktopPresenterImpl;
import org.iplantc.de.client.desktop.presenter.DesktopPresenterWindowEventHandler;
import org.iplantc.de.client.desktop.presenter.DesktopWindowManager;
import org.iplantc.de.client.desktop.presenter.util.MessagePoller;
import org.iplantc.de.client.desktop.views.DesktopViewImpl;
import org.iplantc.de.client.desktop.views.widgets.DEFeedbackDialog;
import org.iplantc.de.client.desktop.views.widgets.PreferencesDialog;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.services.*;
import org.iplantc.de.client.sysmsgs.presenter.NewMessagePresenter;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.client.windows.util.WindowFactory;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.sencha.gxt.widget.core.client.WindowManager;

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

    @Provides @Singleton public AppMetadataServiceFacade createAppMetadataServiceFacade() {
        return ServicesInjector.INSTANCE.getAppMetadataService();
    }

    @Provides @Singleton public AppUserServiceFacade createAppUserServiceFacade(){
        return ServicesInjector.INSTANCE.getAppUserServiceFacade();
    }

    @Provides @Singleton public AppServiceFacade createAppServiceFacade() {
        return ServicesInjector.INSTANCE.getAppServiceFacade();
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

    @Provides @Singleton public KeepaliveTimer createKeepaliveTimer() {
        return KeepaliveTimer.getInstance();
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
        bind(AppsView.class).to(AppsViewImpl.class);

    }
}
