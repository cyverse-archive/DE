package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

public interface DiskResource extends HasId, HasName, HasPath {

    String getInfoType();

    void setId(String id);

    void setPath(String path);

    @Override
    @PropertyName("label")
    String getName();

    @Override
    @PropertyName("label")
    void setName(String name);

    @PropertyName("date-created")
    Date getDateCreated();

    @PropertyName("date-modified")
    Date getLastModified();

    @PropertyName("permission")
    PermissionValue getPermission();

    @PropertyName("filter")
    boolean isFilter();

    @PropertyName("filter")
    void setFilter(boolean filter);

    @PropertyName("isFavorite")
    boolean isFavorite();

    @PropertyName("isFavorite")
    void setFavorite(boolean favorite);

    @PropertyName("share-count")
    int getShareCount();

    @PropertyName("share-count")
    void setShareCount(int count);

    void setStatLoaded(boolean loaded);

    boolean isStatLoaded();
}
