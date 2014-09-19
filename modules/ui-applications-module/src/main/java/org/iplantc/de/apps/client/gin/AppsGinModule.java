package org.iplantc.de.apps.client.gin;

import org.iplantc.de.apps.client.presenter.AppsViewPresenterImpl;
import org.iplantc.de.apps.client.presenter.SubmitAppForPublicPresenter;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.AppsViewImpl;
import org.iplantc.de.apps.client.views.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.views.SubmitAppForPublicUseViewImpl;
import org.iplantc.de.apps.client.views.widgets.AppsViewToolbarImpl;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class AppsGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<TreeStore<AppCategory>>() {}).toProvider(AppCategoryTreeStoreProvider.class);

        bind(new TypeLiteral<Tree<AppCategory, String>>() {
        }).toProvider(AppCategoryTreeProvider.class);

        bind(AppsView.class).to(AppsViewImpl.class);
        bind(AppsView.Presenter.class).to(AppsViewPresenterImpl.class);
        bind(AppsView.ViewMenu.class).to(AppsViewToolbarImpl.class);
        bind(SubmitAppForPublicUseView.class).to(SubmitAppForPublicUseViewImpl.class);
        bind(SubmitAppForPublicUseView.Presenter.class).to(SubmitAppForPublicPresenter.class);

    }

    @Provides
    public AppServiceFacade createAppService() {
        // FIXME Should not be injected here
        return ServicesInjector.INSTANCE.getAppServiceFacade();
    }

    @Provides
    public AppUserServiceFacade createAppUserService() {
        // FIXME Should not be injected here
        return ServicesInjector.INSTANCE.getAppUserServiceFacade();
    }

    @Provides
    public UserInfo createUserInfo() {
        // FIXME Should not be injected here
        return UserInfo.getInstance();
    }

    @Provides
    public EventBus createEventBus() {
        // FIXME Should not be injected here
        return EventBus.getInstance();
    }

    @Provides
    public DEProperties createDeProps() {
        // FIXME Should not be injected here
        return DEProperties.getInstance();
    }
}
