package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * @author jstroot
 */
public interface DiskResource extends HasId, HasName, HasPath {

    String INFO_TYPE_KEY = "infoType";
    String NAME_KEY = "name";

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

    @PropertyName("badName")
    boolean isFilter();

    @PropertyName("badName")
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
