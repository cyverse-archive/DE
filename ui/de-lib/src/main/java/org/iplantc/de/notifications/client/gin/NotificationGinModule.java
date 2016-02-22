package org.iplantc.de.notifications.client.gin;

import org.iplantc.de.notifications.client.gin.factory.NotificationViewFactory;
import org.iplantc.de.notifications.client.presenter.NotificationPresenterImpl;
import org.iplantc.de.notifications.client.views.NotificationToolbarView;
import org.iplantc.de.notifications.client.views.NotificationToolbarViewImpl;
import org.iplantc.de.notifications.client.views.NotificationView;
import org.iplantc.de.notifications.client.views.NotificationViewImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

/**
 * @author aramsey
 */
public class NotificationGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().implement(NotificationView.class, NotificationViewImpl.class).build(
                NotificationViewFactory.class));
        bind(NotificationToolbarView.class).to(NotificationToolbarViewImpl.class);
        bind(NotificationView.Presenter.class).to(NotificationPresenterImpl.class);
    }
}
