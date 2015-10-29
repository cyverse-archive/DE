package org.iplantc.de.client.models.tool;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Created by aramsey on 10/30/15.
 */
public interface ToolImage extends HasName {

    @PropertyName("tag")
    void setTag(String tag);

    @PropertyName("tag")
    String getTag();

    @PropertyName("url")
    void setUrl(String url);

    @PropertyName("url")
    String getUrl();
}
