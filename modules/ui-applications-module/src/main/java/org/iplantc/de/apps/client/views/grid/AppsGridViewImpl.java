package org.iplantc.de.apps.client.views.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by jstroot on 3/5/15.
 */
public class AppsGridViewImpl extends Composite {
    interface AppsGridViewImplUiBinder extends UiBinder<HTMLPanel, AppsGridViewImpl> {
    }

    private static AppsGridViewImplUiBinder ourUiBinder = GWT.create(AppsGridViewImplUiBinder.class);

    public AppsGridViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}