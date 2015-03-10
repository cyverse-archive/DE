package org.iplantc.de.admin.apps.client;

import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author jstroot
 */
public interface AppCategorizeView extends IsWidget {

    public interface AppCategorizeViewAppearance {

        ImageResource category();

        ImageResource category_open();

        String clearSelection();

        String containerWidth();

        String containerHeight();

        ImageResource subCategory();
    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        void setAppCategories(List<AppCategory> children);
    }

    void setAppCategories(List<AppCategory> categories);

    List<AppCategory> getSelectedCategories();

    void setSelectedCategories(List<AppCategory> categories);

    void removeCategoryWithId(String categoryId);
}
