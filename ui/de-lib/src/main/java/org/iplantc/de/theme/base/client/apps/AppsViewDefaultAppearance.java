package org.iplantc.de.theme.base.client.apps;

import org.iplantc.de.apps.client.AppsView;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AppsViewDefaultAppearance implements AppsView.AppsViewAppearance {
    private final AppsMessages appsMessages;

    public AppsViewDefaultAppearance() {
        this(GWT.<AppsMessages> create(AppsMessages.class));
    }

    public AppsViewDefaultAppearance(AppsMessages appsMessages) {
        this.appsMessages = appsMessages;
    }

    @Override
    public String viewCategoriesHeader() {
        return appsMessages.viewCategoriesHeader();
    }
}
