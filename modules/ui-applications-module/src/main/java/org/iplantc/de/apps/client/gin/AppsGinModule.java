package org.iplantc.de.apps.client.gin;

import org.iplantc.de.apps.client.presenter.AppsViewPresenterImpl;
import org.iplantc.de.apps.client.presenter.SubmitAppForPublicPresenter;
import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.views.SubmitAppForPublicUseViewImpl;
import org.iplantc.de.apps.client.views.widgets.AppsViewToolbarImpl;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.impl.AppMetadataServiceFacadeImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class AppsGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<TreeStore<AppCategory>>() {})
            .toProvider(AppCategoryTreeStoreProvider.class);

        bind(new TypeLiteral<Tree<AppCategory, String>>() {})
            .toProvider(AppCategoryTreeProvider.class);

        // KLUDGE Bind AppsView in DEGinModule to get around Gin double-binding with Belphegor
//        bind(AppsView.class).to(AppsViewImpl.class);
        bind(AppsView.Presenter.class).to(AppsViewPresenterImpl.class);
        bind(AppsView.ViewMenu.class).to(AppsViewToolbarImpl.class);
        bind(SubmitAppForPublicUseView.class).to(SubmitAppForPublicUseViewImpl.class);
        bind(SubmitAppForPublicUseView.Presenter.class).to(SubmitAppForPublicPresenter.class);
        bind(AppMetadataServiceFacade.class).to(AppMetadataServiceFacadeImpl.class);
    }

}
