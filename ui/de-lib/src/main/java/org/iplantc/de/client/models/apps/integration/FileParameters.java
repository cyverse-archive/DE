package org.iplantc.de.client.models.apps.integration;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author jstroot
 */
public interface FileParameters {

    String FILE_INFO_TYPE_KEY = "file_info_type";
    String DATA_SOURCE_KEY = "data_source";
    String IS_IMPLICIT_KEY = "is_implicit";
    String REPEAT_OPTION_FLAG = "repeat_option_flag";

    @PropertyName(FILE_INFO_TYPE_KEY)
    FileInfoTypeEnum getFileInfoType();
    
    @PropertyName(FILE_INFO_TYPE_KEY)
    void setFileInfoType(FileInfoTypeEnum fileInfoType);
    
    @PropertyName(DATA_SOURCE_KEY)
    DataSourceEnum getDataSource();

    @PropertyName(DATA_SOURCE_KEY)
    void setDataSource(DataSourceEnum dataSource);

    boolean isRetain();

    void setRetain(boolean retain);

    String getFormat();

    void setFormat(String format);

    @PropertyName(IS_IMPLICIT_KEY)
    boolean isImplicit();

    @PropertyName(IS_IMPLICIT_KEY)
    void setImplicit(boolean implicit);

    @PropertyName(REPEAT_OPTION_FLAG)
    Boolean getRepeatOptionFlag();

    @PropertyName(REPEAT_OPTION_FLAG)
    void setRepeatOptionFlag(Boolean repeat);

}
