package org.iplantc.de.admin.desktop.client.services.model;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface AppCategorizeRequest {

    public interface CategoryRequest {
        @PropertyName("app_id")
        String getAppId();

        @PropertyName("app_id")
        void setAppId(String id);

        @PropertyName("category_ids")
        List<String> getCategories();

        @PropertyName("category_ids")
        void setCategories(List<String> categories);
    }

    List<CategoryRequest> getCategories();

    void setCategories(List<CategoryRequest> categories);


}
