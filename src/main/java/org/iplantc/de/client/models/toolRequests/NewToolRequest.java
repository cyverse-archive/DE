package org.iplantc.de.client.models.toolRequests;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * This is a model of a tool request.
 * 
 * Either the source URL or source file needs to be set for this to be a valid tool request.
 * 
 * <a href=
 * "https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/master/doc/endpoints/app-metadata/tool-requests.md#requesting-tool-installation"
 * />
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

    @PropertyName("src_url")
    String getSourceURL();

    @PropertyName("src_url")
    void setSourceURL(String url);

    @PropertyName("src_upload_file")
    String getSourceFile();

    @PropertyName("src_upload_file")
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
    YesNoMaybe getMultithreaded();

    void setMultithreaded(YesNoMaybe multithreaded);

    @PropertyName("test_data_file")
    String getTestDataFile();

    @PropertyName("test_data_file")
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
