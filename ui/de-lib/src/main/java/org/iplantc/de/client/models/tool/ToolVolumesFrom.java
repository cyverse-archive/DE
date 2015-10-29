package org.iplantc.de.client.models.tool;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Created by aramsey on 10/30/15.
 */
public interface ToolVolumesFrom extends HasName, HasId {

    @PropertyName("id")
    void setId(String id);

    @PropertyName("tag")
    void setTag(String tag);

    @PropertyName("tag")
    String getTag();

    @PropertyName("url")
    void setUrl(String url);

    @PropertyName("url")
    String getUrl();

    @PropertyName("name_prefix")
    void setNamePrefix(String namePrefix);

    @PropertyName("name_prefix")
    String getNamePrefix();

    @PropertyName("read_only")
    void setReadOnly(Boolean readOnly);

    @PropertyName("read_only")
    Boolean isReadOnly();
}
