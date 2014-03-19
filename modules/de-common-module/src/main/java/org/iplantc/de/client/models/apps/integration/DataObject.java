package org.iplantc.de.client.models.apps.integration;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface DataObject {

    @PropertyName("file_info_type")
    FileInfoTypeEnum getFileInfoType();
    
    @PropertyName("file_info_type")
    void setFileInfoType(FileInfoTypeEnum fileInfoType);
    
    @PropertyName("data_source")
    DataSourceEnum getDataSource();

    @PropertyName("data_source")
    void setDataSource(DataSourceEnum dataSource);

    boolean isRetain();

    void setRetain(boolean retain);

    String getFormat();

    void setFormat(String format);

    @PropertyName("is_implicit")
    boolean isImplicit();

    @PropertyName("is_implicit")
    void setImplicit(boolean implicit);

    void setCmdSwitch(String string);

    String getCmdSwitch();
}
