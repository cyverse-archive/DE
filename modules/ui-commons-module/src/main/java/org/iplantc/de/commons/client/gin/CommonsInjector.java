package org.iplantc.de.commons.client.gin;

import org.iplantc.de.commons.client.tags.views.IplantTagListView;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(CommonsGinModule.class)
public interface CommonsInjector extends Ginjector {
    
    public static final CommonsInjector INSTANCE = GWT.create(CommonsInjector.class);

    public IplantTagListView getIplantTagListView();

}
