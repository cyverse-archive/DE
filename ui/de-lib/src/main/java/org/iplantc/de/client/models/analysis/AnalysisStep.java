package org.iplantc.de.client.models.analysis;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface AnalysisStep {

    @PropertyName("step_number")
    int getStepNumber();

    @PropertyName("step_type")
    String getStepType();

    @PropertyName("step_type")
    void setStepType(String type);

    @PropertyName("external_id")
    String getId();

    @PropertyName("external_id")
    void setId(String id);

    void setStatus(String status);

    String getStatus();

}
