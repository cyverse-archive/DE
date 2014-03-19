package org.iplantc.de.diskResource.client.gin;

import org.iplantc.de.diskResource.client.views.DiskResourceView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(DiskResourceGinModule.class)
public interface DiskResourceInjector extends Ginjector {
    public static final DiskResourceInjector INSTANCE = GWT.create(DiskResourceInjector.class);

    public DiskResourceView.Presenter getDiskResourceViewPresenter();

}
