package org.iplantc.de.client.gin;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.newDesktop.NewDesktopView;
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

public class DEGinModule extends AbstractGinModule {
    @Provides
    @Singleton
    public EventBus createGlobalEventBus(){

        return EventBus.getInstance();
    }

    @Provides
    @Singleton
    public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

    @Override
    protected void configure() {
        bind(NewDesktopView.class).to(NewDesktopViewImpl.class);
        bind(NewDesktopView.Presenter.class).to(NewDesktopPresenterImpl.class);

        bind(NewMessageView.Presenter.class).to(NewMessagePresenter.class);
    }

    @Provides
    public AnalysisServiceFacade createAnalysisService() {
        return ServicesInjector.INSTANCE.getAnalysisServiceFacade();
    }

    @Provides
    public FileEditorServiceFacade createFileEditorService() {
        return ServicesInjector.INSTANCE.getFileEditorServiceFacade();
    }

    @Provides
    public DiskResourceServiceFacade createDiskResourceService() {
        return ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
    }

    @Provides
    public UserSessionServiceFacade createUserSessionServiceFacade(){
        return ServicesInjector.INSTANCE.getUserSessionServiceFacade();
    }

    @Provides
    public PropertyServiceFacade createPropertyServiceFacade() {
        return PropertyServiceFacade.getInstance();
    }

    @Provides
    public IplantResources createIplantResources(){
        return IplantResources.RESOURCES;
    }
}
