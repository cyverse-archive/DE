package org.iplantc.de.client.models.toolRequests;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * This is a model of a tool request.
 * 
 * Either the source URL or source file needs to be set for this to be a valid tool request.
 * 
 */
public interface NewToolRequest {

    /**
     * This is optional.
     */
    @PropertyName("phone")
    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    @PropertyName("source_url")
    String getSourceURL();

    @PropertyName("source_url")
    void setSourceURL(String url);

    @PropertyName("source_upload_file")
    String getSourceFile();

    @PropertyName("source_upload_file")
    void setSourceFile(final String filePath);

    @PropertyName("documentation_url")
    String getDocURL();

    @PropertyName("documentation_url")
    void setDocURL(String url);

    String getVersion();

    void setVersion(String version);

    /**
     * This is optional.
     */
    String getAttribution();

    void setAttribution(String attribution);

    /**
     * This is optional.
     */
    Boolean getMultithreaded();

    void setMultithreaded(Boolean multithreaded);

    @PropertyName("test_data_path")
    String getTestDataFile();

    @PropertyName("test_data_path")
    void setTestDataFile(String filePath);

    @PropertyName("cmd_line")
    String getInstructions();

    @PropertyName("cmd_line")
    void setInstructions(String instructions);

    /**
     * This is optional.
     */
    @PropertyName("additional_info")
    String getAdditionalInfo();

    @PropertyName("additional_info")
    void setAdditionaInfo(String info);

    /**
     * This is optional.
     */
    @PropertyName("additional_data_file")
    String getAdditionalDataFile();

    @PropertyName("additional_data_file")
    void setAdditionalDataFile(String filePath);

    Architecture getArchitecture();

    void setArchitecture(Architecture architecture);

}
