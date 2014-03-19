package org.iplantc.de.client.models.viewer;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface StructuredText {

    @PropertyName("chunk-size")
    public void setChunkSize(String size);

    @PropertyName("chunk-size")
    public String getChunkSize();

    @PropertyName("max-cols")
    public void setMaxColumns(String maxCols);

    @PropertyName("max-cols")
    public String getMaxColumns();

    @PropertyName("page")
    public String getPage();

    @PropertyName("page")
    public void setPage(String page);

}
