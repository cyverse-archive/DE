package org.iplantc.de.theme.base.client.apps.categories;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.widget.core.client.tree.TreeStyle;

/**
 * @author jstroot
 */
public class AppCategoriesViewDefaultAppearance implements AppCategoriesView.AppCategoriesAppearance {

    public interface AppCategoryViewResources extends ClientBundle {

        @Source("../book_add.png")
        ImageResource category();

        @Source("../book_open.png")
        ImageResource categoryOpen();

        @Source("../book.png")
        ImageResource subCategory();
    }

    private final AppCategoryViewResources resources;
    private final IplantDisplayStrings iplantDisplayStrings;

    public AppCategoriesViewDefaultAppearance() {
        this(GWT.<AppCategoryViewResources> create(AppCategoryViewResources.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    AppCategoriesViewDefaultAppearance(final AppCategoryViewResources resources,
                                       final IplantDisplayStrings iplantDisplayStrings) {
        this.resources = resources;
        this.iplantDisplayStrings = iplantDisplayStrings;
    }


    @Override
    public String headingText() {
        return iplantDisplayStrings.category();
    }

    @Override
    public void setTreeIcons(TreeStyle style) {
        style.setNodeCloseIcon(resources.category());
        style.setNodeOpenIcon(resources.categoryOpen());
        style.setLeafIcon(resources.subCategory());
    }
}
