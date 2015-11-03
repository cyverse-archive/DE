package org.iplantc.de.client.models.toolRequests;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * The details of a tool request once it has been submitted.
 * 
 * @link https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/master/doc/endpoints/app-
 *       metadata/tool-requests.md#obtaining-tool-request-details
 */
public interface RequestedToolDetails {

    /**
     * This is optional.
     */
    @PropertyName("phone")
    String getPhoneNumber();

    String getName();

    String getDescription();

    @PropertyName("source_url")
    String getSource();

    @PropertyName("documentation_url")
    String getDocURL();

    String getVersion();

    /**
     * This is optional.
     */
    String getAttribution();

    /**
     * This is optional.
     */
    String getMultithreaded();

    @PropertyName("test_data_path")
    String getTestDataFile();

    @PropertyName("cmd_line")
    String getInstructions();

    /**
     * This is optional.
     */
    @PropertyName("additional_data_file")
    String getAdditionalDataFile();

    /**
     * This is optional.
     */
    @PropertyName("additional_info")
    String getAdditionalInfo();

    Architecture getArchitecture();

    List<StatusChange> getHistory();

    @PropertyName("submitted_by")
    String getSubmitterName();

    boolean isSuccess();

    String getUUID();

}
