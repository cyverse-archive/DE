package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface AppCategory extends HasId, HasName {

    @PropertyName("app_count")
    int getAppCount();

    List<AppCategory> getCategories();

    @PropertyName("is_public")
    boolean isPublic();

    @PropertyName("app_count")
    void setAppCount(int templateCount);

    void setCategories(List<AppCategory> categories);

    @PropertyName("is_public")
    void setIsPublic(boolean isPublic);

    void setId(String id);

}
