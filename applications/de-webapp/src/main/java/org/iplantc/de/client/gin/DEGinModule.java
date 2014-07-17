package org.iplantc.de.client.gin;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.newDesktop.presenter.DesktopPresenterEventHandler;
import org.iplantc.de.client.newDesktop.presenter.DesktopPresenterWindowEventHandler;
import org.iplantc.de.client.newDesktop.presenter.NewDesktopPresenterImpl;
import org.iplantc.de.client.newDesktop.views.NewDesktopViewImpl;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.sysmsgs.presenter.NewMessagePresenter;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.shared.services.PropertyServiceFacade;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.sencha.gxt.widget.core.client.WindowManager;

public class DEGinModule extends AbstractGinModule {
    @Provides public AnalysisServiceFacade createAnalysisService() {
        return ServicesInjector.INSTANCE.getAnalysisServiceFacade();
    }

    @Provides @Singleton public DiskResourceServiceFacade createDiskResourceService() {
        return ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
    }

    @Provides public FileEditorServiceFacade createFileEditorService() {
        return ServicesInjector.INSTANCE.getFileEditorServiceFacade();
    }

    @Provides @Singleton public EventBus createGlobalEventBus() {
        return EventBus.getInstance();
    }

    @Provides public IplantResources createIplantResources() {
        return IplantResources.RESOURCES;
    }

    @Provides public PropertyServiceFacade createPropertyServiceFacade() {
        return PropertyServiceFacade.getInstance();
    }

    @Provides @Singleton public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

    @Provides public UserSessionServiceFacade createUserSessionServiceFacade() {
        return ServicesInjector.INSTANCE.getUserSessionServiceFacade();
    }

    @Provides @Singleton public UserSettings createUserSettings() {
        return UserSettings.getInstance();
    }

    @Provides @Singleton public DEProperties getDeProperties() {
        return DEProperties.getInstance();
    }

    @Provides public WindowManager getWindowManager() {
        return WindowManager.get();
    }

    @Override
    protected void configure() {
        bind(NewDesktopView.class).to(NewDesktopViewImpl.class);
        bind(NewDesktopView.Presenter.class).to(NewDesktopPresenterImpl.class);

        bind(NewMessageView.Presenter.class).to(NewMessagePresenter.class);
        bind(DesktopPresenterEventHandler.class);
        bind(DesktopPresenterWindowEventHandler.class);
    }
}
