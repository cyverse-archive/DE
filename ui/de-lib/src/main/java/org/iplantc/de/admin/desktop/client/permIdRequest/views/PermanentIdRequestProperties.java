package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestType;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import java.util.Date;

/**
 * 
 * 
 * @author sriram
 * 
 */

public interface PermanentIdRequestProperties extends PropertyAccess<PermanentIdRequest> {

    ModelKeyProvider<PermanentIdRequest> id();
    
    ValueProvider<PermanentIdRequest, String> requestedBy();

    ValueProvider<PermanentIdRequest, Date> dateSubmitted();

    ValueProvider<PermanentIdRequest, Date> dateUpdated();

    ValueProvider<PermanentIdRequest, PermanentIdRequestType> type();

    ValueProvider<PermanentIdRequest, String> status();

}
