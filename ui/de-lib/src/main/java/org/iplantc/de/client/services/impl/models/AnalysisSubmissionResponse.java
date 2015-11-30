package org.iplantc.de.client.services.impl.models;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;
import java.util.List;

/**
 * @author psarando
 */
public interface AnalysisSubmissionResponse extends HasId, HasName {

    String getStatus();

    @PropertyName("start-date")
    Date getStartDate();

    @PropertyName("missing-paths")
    List<String> getMissingPaths();
}
