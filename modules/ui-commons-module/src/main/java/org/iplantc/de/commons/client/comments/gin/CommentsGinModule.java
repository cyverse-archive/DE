package org.iplantc.de.commons.client.comments.gin;

import org.iplantc.de.commons.client.comments.CommentsView;
import org.iplantc.de.commons.client.comments.gin.factory.CommentsPresenterFactory;
import org.iplantc.de.commons.client.comments.presenter.CommentsPresenterImpl;
import org.iplantc.de.commons.client.comments.view.CommentsViewImpl;
import org.iplantc.de.commons.client.comments.view.dialogs.CommentsDialog;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

/**
 * @author jstroot
 */
public class CommentsGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(CommentsView.class).to(CommentsViewImpl.class);

        install(new GinFactoryModuleBuilder()
                    .implement(CommentsView.Presenter.class, CommentsPresenterImpl.class)
                    .build(CommentsPresenterFactory.class));

        bind(CommentsDialog.class);
    }
}
