package org.iplantc.de.diskResource.client.gin;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.DataLinkView;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.SearchView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.gin.factory.BulkMetadataDialogFactory;
import org.iplantc.de.diskResource.client.gin.factory.DataLinkPresenterFactory;
import org.iplantc.de.diskResource.client.gin.factory.DataLinkViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.DetailsViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourcePresenterFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.FolderContentsRpcProxyFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewPresenterFactory;
import org.iplantc.de.diskResource.client.gin.factory.NavigationViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.ToolbarViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.ToolbarViewPresenterFactory;
import org.iplantc.de.diskResource.client.presenters.DiskResourcePresenterImpl;
import org.iplantc.de.diskResource.client.presenters.dataLink.DataLinkPresenterImpl;
import org.iplantc.de.diskResource.client.presenters.details.DetailsViewPresenterImpl;
import org.iplantc.de.diskResource.client.presenters.grid.GridViewPresenterImpl;
import org.iplantc.de.diskResource.client.presenters.grid.proxy.FolderContentsRpcProxyImpl;
import org.iplantc.de.diskResource.client.presenters.navigation.NavigationPresenterImpl;
import org.iplantc.de.diskResource.client.presenters.navigation.proxy.FolderRpcProxyImpl;
import org.iplantc.de.diskResource.client.presenters.search.DataSearchPresenterImpl;
import org.iplantc.de.diskResource.client.presenters.toolbar.ToolbarViewPresenterImpl;
import org.iplantc.de.diskResource.client.views.DiskResourceViewImpl;
import org.iplantc.de.diskResource.client.views.dataLink.DataLinkViewImpl;
import org.iplantc.de.diskResource.client.views.details.DetailsViewImpl;
import org.iplantc.de.diskResource.client.views.dialogs.BulkMetadataDialog;
import org.iplantc.de.diskResource.client.views.dialogs.GenomeSearchDialog;
import org.iplantc.de.diskResource.client.views.dialogs.InfoTypeEditorDialog;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.diskResource.client.views.grid.GridViewImpl;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.ManageMetadataDialog;
import org.iplantc.de.diskResource.client.views.navigation.NavigationViewImpl;
import org.iplantc.de.diskResource.client.views.search.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.search.cells.DiskResourceQueryForm;
import org.iplantc.de.diskResource.client.views.search.cells.DiskResourceSearchCell;
import org.iplantc.de.diskResource.client.views.sharing.dialogs.DataSharingDialog;
import org.iplantc.de.diskResource.client.views.sharing.dialogs.ShareResourceLinkDialog;
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
                    .implement(GridView.FolderContentsRpcProxy.class, FolderContentsRpcProxyImpl.class)
                    .build(FolderContentsRpcProxyFactory.class));

        // Disk Resource Presenters
        bind(SearchView.Presenter.class).to(DataSearchPresenterImpl.class);
        install(new GinFactoryModuleBuilder()
                    .implement(DiskResourceView.Presenter.class, DiskResourcePresenterImpl.class)
                    .build(DiskResourcePresenterFactory.class));

        // Data Links
        install(new GinFactoryModuleBuilder()
                    .implement(DataLinkView.Presenter.class, DataLinkPresenterImpl.class)
                    .build(DataLinkPresenterFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(DataLinkView.class, DataLinkViewImpl.class)
                    .build(DataLinkViewFactory.class));

        // Disk Resource Views
        bind(new TypeLiteral<TreeStore<Folder>>() {}).toProvider(DiskResourceTreeStoreProvider.class);
        install(new GinFactoryModuleBuilder()
                    .implement(DiskResourceView.class, DiskResourceViewImpl.class)
                    .build(DiskResourceViewFactory.class));

        // Disk Resource Selectors and Dialogs
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

        // Toolbar
        install(new GinFactoryModuleBuilder()
                    .implement(ToolbarView.class, DiskResourceViewToolbarImpl.class)
                    .build(ToolbarViewFactory.class));

        install(new GinFactoryModuleBuilder()
                    .implement(ToolbarView.Presenter.class, ToolbarViewPresenterImpl.class)
                    .build(ToolbarViewPresenterFactory.class));

        // Details
        bind(DetailsView.Presenter.class).to(DetailsViewPresenterImpl.class);
        install(new GinFactoryModuleBuilder()
                    .implement(DetailsView.class, DetailsViewImpl.class)
                    .build(DetailsViewFactory.class));

        install(new GinFactoryModuleBuilder().build(BulkMetadataDialogFactory.class));

        // Dialogs
        bind(InfoTypeEditorDialog.class);
        bind(ManageMetadataDialog.class);
        bind(DataSharingDialog.class);
        bind(ShareResourceLinkDialog.class);
        bind(SaveAsDialog.class);

        bind(GenomeSearchDialog.class);
    }


}
