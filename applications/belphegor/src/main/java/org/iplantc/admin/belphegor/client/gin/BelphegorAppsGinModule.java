package org.iplantc.admin.belphegor.client.gin;

import org.iplantc.admin.belphegor.client.BelphegorResources;
import org.iplantc.admin.belphegor.client.apps.views.AdminAppViewImpl;
import org.iplantc.admin.belphegor.client.apps.views.AdminAppsView;
import org.iplantc.admin.belphegor.client.apps.views.widgets.BelphegorAppsToolbarImpl;
import org.iplantc.admin.belphegor.client.models.BelphegorAdminProperties;
import org.iplantc.admin.belphegor.client.presenter.BelphegorPresenterImpl;
import org.iplantc.admin.belphegor.client.refGenome.RefGenomeView;
import org.iplantc.admin.belphegor.client.refGenome.presenter.RefGenomePresenterImpl;
import org.iplantc.admin.belphegor.client.refGenome.service.ReferenceGenomeServiceFacade;
import org.iplantc.admin.belphegor.client.refGenome.service.impl.ReferenceGenomeServiceFacadeImpl;
import org.iplantc.admin.belphegor.client.refGenome.view.RefGenomeViewImpl;
import org.iplantc.admin.belphegor.client.services.AppAdminServiceFacade;
import org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacadeImpl;
import org.iplantc.admin.belphegor.client.services.impl.AppAdminUserServiceFacade;
import org.iplantc.admin.belphegor.client.systemMessage.SystemMessageView;
import org.iplantc.admin.belphegor.client.systemMessage.presenter.SystemMessagePresenterImpl;
import org.iplantc.admin.belphegor.client.systemMessage.service.SystemMessageServiceFacade;
import org.iplantc.admin.belphegor.client.systemMessage.service.impl.SystemMessageServiceFacadeImpl;
import org.iplantc.admin.belphegor.client.systemMessage.view.SystemMessageViewImpl;
import org.iplantc.admin.belphegor.client.toolRequest.ToolRequestView;
import org.iplantc.admin.belphegor.client.toolRequest.presenter.ToolRequestPresenterImpl;
import org.iplantc.admin.belphegor.client.toolRequest.service.ToolRequestServiceFacade;
import org.iplantc.admin.belphegor.client.toolRequest.service.impl.ToolRequestServiceFacadeImpl;
import org.iplantc.admin.belphegor.client.toolRequest.view.ToolRequestViewImpl;
import org.iplantc.admin.belphegor.client.views.BelphegorView;
import org.iplantc.admin.belphegor.client.views.BelphegorViewImpl;
import org.iplantc.de.apps.client.gin.AppGroupTreeProvider;
import org.iplantc.de.apps.client.gin.AppGroupTreeStoreProvider;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class BelphegorAppsGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(BelphegorView.class).to(BelphegorViewImpl.class);
        bind(BelphegorView.Presenter.class).to(BelphegorPresenterImpl.class).in(Singleton.class);

        bind(new TypeLiteral<TreeStore<AppGroup>>() {
        }).toProvider(AppGroupTreeStoreProvider.class).in(Singleton.class);

        bind(new TypeLiteral<Tree<AppGroup, String>>() {
        }).toProvider(AppGroupTreeProvider.class).in(Singleton.class);

        bind(AppsView.class).to(AdminAppViewImpl.class);
        bind(AdminAppsView.Toolbar.class).to(BelphegorAppsToolbarImpl.class);

        bind(RefGenomeView.class).to(RefGenomeViewImpl.class);
        bind(RefGenomeView.Presenter.class).to(RefGenomePresenterImpl.class);
        bind(ReferenceGenomeServiceFacade.class).to(ReferenceGenomeServiceFacadeImpl.class);

        bind(ToolRequestView.class).to(ToolRequestViewImpl.class);
        bind(ToolRequestView.Presenter.class).to(ToolRequestPresenterImpl.class);
        bind(ToolRequestServiceFacade.class).to(ToolRequestServiceFacadeImpl.class);

        bind(SystemMessageView.class).to(SystemMessageViewImpl.class);
        bind(SystemMessageView.Presenter.class).to(SystemMessagePresenterImpl.class);
        bind(SystemMessageServiceFacade.class).to(SystemMessageServiceFacadeImpl.class);

        bind(BelphegorResources.class).in(Singleton.class);

        bind(AppUserServiceFacade.class).to(AppAdminUserServiceFacade.class);
        bind(AppAdminServiceFacade.class).to(AppAdminServiceFacadeImpl.class);
        bind(AppServiceFacade.class).to(AppAdminServiceFacadeImpl.class);
    }

    @Provides
    public EventBus createEventBus(){
        return EventBus.getInstance();
    }

    @Provides
    public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

    @Provides
    public DEProperties createDeProps() {
        return DEProperties.getInstance();
    }

    @Provides public BelphegorAdminProperties getAdminProperties() {
        return BelphegorAdminProperties.getInstance();
    }
}
