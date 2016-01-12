package org.iplantc.de.client.models.requestStatus;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

/**
 * A PropertyAccess interface for ToolRequestHistory AutoBeans.
 * 
 * @author psarando
 * 
 */
public interface RequestHistoryProperties extends PropertyAccess<RequestHistory> {

    ValueProvider<RequestHistory, String> status();

    ValueProvider<RequestHistory, String> updatedBy();

    ValueProvider<RequestHistory, Date> statusDate();

    ValueProvider<RequestHistory, String> comments();
}
