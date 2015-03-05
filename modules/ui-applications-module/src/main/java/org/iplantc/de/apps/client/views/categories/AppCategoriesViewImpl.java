package org.iplantc.de.apps.client.views.categories;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by jstroot on 3/5/15.
 */
public class AppCategoriesViewImpl extends Composite {
    interface AppCategoriesViewImplUiBinder extends UiBinder<HTMLPanel, AppCategoriesViewImpl> {
    }

    private static AppCategoriesViewImplUiBinder ourUiBinder = GWT.create(AppCategoriesViewImplUiBinder.class);

    public AppCategoriesViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}