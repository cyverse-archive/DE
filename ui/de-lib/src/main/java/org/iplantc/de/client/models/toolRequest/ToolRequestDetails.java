package org.iplantc.de.client.models.toolRequest;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author jstroot
 * 
 */
public interface ToolRequestDetails extends HasId, HasName, HasDescription {

    @PropertyName("additional_data_file")
    String getAdditionalDataFile();

    @PropertyName("additional_info")
    String getAdditionalInfo();

    String getArchitecture();

    String getAttribution();

    @PropertyName("cmd_line")
    String getCmdLineDescription();

    @PropertyName("documentation_url")
    String getDocumentationUrl();

    List<ToolRequestHistory> getHistory();

    boolean getMultiThreaded();

    String getPhone();

    @PropertyName("source_url")
    String getSourceUrl();

    @PropertyName("submitted_by")
    String getSubmittedBy();

    @PropertyName("test_data_path")
    String getTestDataPath();

    @Override
    @PropertyName("id")
    String getId();

    String getVersion();

}
