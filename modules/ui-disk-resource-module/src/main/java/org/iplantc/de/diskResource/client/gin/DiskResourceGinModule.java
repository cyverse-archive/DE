package org.iplantc.de.diskResource.client.gin;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.dataLink.presenter.DataLinkPresenterImpl;
import org.iplantc.de.diskResource.client.dataLink.view.DataLinkPanel;
import org.iplantc.de.diskResource.client.gin.factory.*;
import org.iplantc.de.diskResource.client.presenters.DiskResourcePresenterImpl;
import org.iplantc.de.diskResource.client.presenters.grid.GridViewPresenterImpl;
import org.iplantc.de.diskResource.client.presenters.navigation.NavigationPresenterImpl;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsRpcProxyImpl;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderRpcProxyImpl;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.presenter.impl.DataSearchPresenterImpl;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.search.views.cells.DiskResourceQueryForm;
import org.iplantc.de.diskResource.client.search.views.cells.DiskResourceSearchCell;
import org.iplantc.de.diskResource.client.views.DiskResourceViewImpl;
import org.iplantc.de.diskResource.client.views.grid.GridViewImpl;
import org.iplantc.de.diskResource.client.views.navigation.NavigationViewImpl;
import org.iplantc.de.diskResource.client.views.toolbar.DiskResourceViewToolbarImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author jstroot
 */
public class DiskResourceGinModule extends AbstractGinModule {

    @Override
    protected void configure() {

        // RPC Proxies
        bind(DiskResourceView.FolderRpcProxy.class).to(FolderRpcProxyImpl.class);
        install(new GinFactoryModuleBuilder()
                    .implement(DiskResourceView.FolderContentsRpcProxy.class, FolderContentsRpcProxyImpl.class)
                    .build(FolderContentsRpcProxyFactory.class));

        // Disk Resource Presenters
        bind(DataSearchPresenter.class).to(DataSearchPresenterImpl.class);
        install(new GinFactoryModuleBuilder()
                    .implement(DiskResourceView.Presenter.class, DiskResourcePresenterImpl.class)
                    .build(DiskResourcePresenterFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(DataLinkPanel.Presenter.class, DataLinkPresenterImpl.class)
                    .build(DataLinkPanelFactory.class));

        // Disk Resource Views
        bind(new TypeLiteral<TreeStore<Folder>>() {}).toProvider(DiskResourceTreeStoreProvider.class);
        bind(ToolbarView.class).to(DiskResourceViewToolbarImpl.class);
        install(new GinFactoryModuleBuilder()
                    .implement(DiskResourceView.class, DiskResourceViewImpl.class)
                    .build(DiskResourceViewFactory.class));

        // Disk Resource Selectors and Dialogs
        install(new GinFactoryModuleBuilder()
                    .build(DiskResourceSelectorDialogFactory.class));
        install(new GinFactoryModuleBuilder()
                    .build(DataSharingDialogFactory.class));
        install(new GinFactoryModuleBuilder()
                    .build(DiskResourceSelectorFieldFactory.class));
        bind(DiskResourceQueryForm.class);
        bind(DiskResourceSearchCell.class);
        bind(DiskResourceSearchField.class);


        // Navigation View/Presenter
        bind(NavigationView.Presenter.class).to(NavigationPresenterImpl.class);
        install(new GinFactoryModuleBuilder()
                    .implement(NavigationView.class, NavigationViewImpl.class)
                    .build(NavigationViewFactory.class));


        // Grid View/Presenter
        install(new GinFactoryModuleBuilder()
                    .implement(GridView.Presenter.class, GridViewPresenterImpl.class)
                    .build(GridViewPresenterFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(GridView.class, GridViewImpl.class)
                    .build(GridViewFactory.class));
    }


}
