package org.iplantc.admin.belphegor.client.toolRequest.view;

import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestStatus;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

public interface ToolRequestProperties extends PropertyAccess<ToolRequest> {

    ModelKeyProvider<ToolRequest> id();

    ValueProvider<ToolRequest, String> name();

    ValueProvider<ToolRequest, ToolRequestStatus> status();

    ValueProvider<ToolRequest, Date> dateSubmitted();

    ValueProvider<ToolRequest, Date> dateUpdated();

    ValueProvider<ToolRequest, String> updatedBy();

    ValueProvider<ToolRequest, String> version();

}
