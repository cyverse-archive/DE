package org.iplantc.de.admin.desktop.client.permIdRequest.view;

import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestType;

import com.google.gwt.editor.client.Editor.Path;

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

    @Path("Folder.path")
    ValueProvider<PermanentIdRequest, String> path();

    ValueProvider<PermanentIdRequest, PermanentIdRequestType> type();

    ValueProvider<PermanentIdRequest, String> status();

}
