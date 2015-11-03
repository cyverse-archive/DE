package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.apps.client.AppCategorizeView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class AppCategorizeViewDefaultAppearance implements AppCategorizeView.AppCategorizeViewAppearance {
    private final BelphegorDisplayStrings displayStrings;
    private final IplantResources resources;

    public AppCategorizeViewDefaultAppearance() {
        this(GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    AppCategorizeViewDefaultAppearance(final BelphegorDisplayStrings displayStrings,
                                       final IplantResources resources) {
        this.displayStrings = displayStrings;
        this.resources = resources;
    }

    @Override
    public ImageResource category() {
        return resources.category();
    }

    @Override
    public ImageResource category_open() {
        return resources.category_open();
    }

    @Override
    public String clearSelection() {
        return displayStrings.clearSelection();
    }

    @Override
    public String containerWidth() {
        return "400";
    }

    @Override
    public String containerHeight() {
        return "400";
    }

    @Override
    public ImageResource subCategory() {
        return resources.subCategory();
    }
}
