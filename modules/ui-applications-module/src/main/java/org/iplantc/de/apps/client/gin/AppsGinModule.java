package org.iplantc.de.apps.client.gin;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.gin.factory.AppDetailsViewFactory;
import org.iplantc.de.apps.client.gin.factory.AppDetailsViewPresenterFactory;
import org.iplantc.de.apps.client.gin.factory.AppsViewFactory;
import org.iplantc.de.apps.client.presenter.AppsViewPresenterImpl;
import org.iplantc.de.apps.client.presenter.details.AppDetailsViewPresenterImpl;
import org.iplantc.de.apps.client.presenter.submit.SubmitAppForPublicPresenter;
import org.iplantc.de.apps.client.views.AppsViewImpl;
import org.iplantc.de.apps.client.views.details.AppDetailsViewImpl;
import org.iplantc.de.apps.client.views.details.dialogs.AppDetailsDialog;
import org.iplantc.de.apps.client.views.submit.SubmitAppForPublicUseViewImpl;
import org.iplantc.de.apps.client.views.toolBar.AppsViewToolbarImpl;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.impl.AppMetadataServiceFacadeImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author jstroot
 */
public class AppsGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(new TypeLiteral<TreeStore<AppCategory>>() {})
            .toProvider(AppCategoryTreeStoreProvider.class);

        // KLUDGE Bind AppsView in DEGinModule to get around Gin double-binding with Belphegor
//        bind(AppsView.class).to(AppsViewImpl.class);
        bind(AppsView.Presenter.class).to(AppsViewPresenterImpl.class);
        bind(AppsToolbarView.class).to(AppsViewToolbarImpl.class);
        bind(SubmitAppForPublicUseView.class).to(SubmitAppForPublicUseViewImpl.class);
        bind(SubmitAppForPublicUseView.Presenter.class).to(SubmitAppForPublicPresenter.class);
        bind(AppMetadataServiceFacade.class).to(AppMetadataServiceFacadeImpl.class);

        install(new GinFactoryModuleBuilder()
                    .implement(AppsView.class, AppsViewImpl.class)
                    .build(AppsViewFactory.class));

        // Details
        install(new GinFactoryModuleBuilder()
                    .implement(AppDetailsView.Presenter.class, AppDetailsViewPresenterImpl.class)
                    .build(AppDetailsViewPresenterFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(AppDetailsView.class, AppDetailsViewImpl.class)
                    .build(AppDetailsViewFactory.class));
        bind(AppDetailsDialog.class);
    }

}
