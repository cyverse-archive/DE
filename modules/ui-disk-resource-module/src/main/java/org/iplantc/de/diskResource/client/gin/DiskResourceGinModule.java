package org.iplantc.de.diskResource.client.gin;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.TagsServiceFacade;
import org.iplantc.de.diskResource.client.dataLink.presenter.DataLinkPresenterImpl;
import org.iplantc.de.diskResource.client.dataLink.view.DataLinkPanel;
import org.iplantc.de.diskResource.client.gin.factory.DataLinkPanelFactory;
import org.iplantc.de.diskResource.client.gin.factory.DataSharingDialogFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourcePresenterFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorDialogFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.FolderRpcProxyFactory;
import org.iplantc.de.diskResource.client.presenters.DiskResourcePresenterImpl;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsRpcProxyImpl;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderRpcProxyImpl;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.presenter.impl.DataSearchPresenterImpl;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.diskResource.client.views.DiskResourceViewImpl;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceViewToolbarImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class DiskResourceGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<TreeStore<Folder>>() {}).toProvider(DiskResourceTreeStoreProvider.class);
        bind(new TypeLiteral<Tree<Folder, Folder>>() {}).toProvider(DiskResourceTreeProvider.class);
        bind(DiskResourceView.DiskResourceViewToolbar.class).to(DiskResourceViewToolbarImpl.class);
        bind(DataSearchPresenter.class).to(DataSearchPresenterImpl.class);

        bind(DiskResourceView.FolderContentsRpcProxy.class).to(FolderContentsRpcProxyImpl.class);
        install(new GinFactoryModuleBuilder()
                    .implement(DiskResourceView.FolderRpcProxy.class, FolderRpcProxyImpl.class)
                    .build(FolderRpcProxyFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(DataSearchPresenter.class, DataSearchPresenterImpl.class)
                    .implement(DiskResourceView.class, DiskResourceViewImpl.class)
                    .build(DiskResourceViewFactory.class));
        // DiskResource Selectors
        install(new GinFactoryModuleBuilder()
                    .build(DiskResourceSelectorDialogFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(DiskResourceView.Presenter.class, DiskResourcePresenterImpl.class)
                    .build(DiskResourcePresenterFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(DataLinkPanel.Presenter.class, DataLinkPresenterImpl.class)
                    .build(DataLinkPanelFactory.class));
        install(new GinFactoryModuleBuilder()
                    .build(DataSharingDialogFactory.class));
    }

    @Provides
    @Singleton
    public SearchServiceFacade createSearchServiceFacade() {
        return ServicesInjector.INSTANCE.getSearchServiceFacade();
    }

    @Provides
    public TagsServiceFacade createMetadataServiceFacade() {
        return ServicesInjector.INSTANCE.getMetadataService();
    }

    @Provides
    public MetadataServiceFacade createFileSystemMetadataServiceFacade() {
        return ServicesInjector.INSTANCE.getFileSysteMetadataServiceFacade();
    }
}
