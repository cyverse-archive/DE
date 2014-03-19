package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;
import java.util.List;

public interface App extends HasId, HasName, HasDescription {
    
    @PropertyName("is_favorite")
    boolean isFavorite();

    @PropertyName("wiki_url")
    String getWikiUrl();
    
    @PropertyName("integrator_name")
    String getIntegratorName();
    
    AppFeedback getRating();
    
    @PropertyName("integration_date")
    Date getIntegrationDate();
    
    @PropertyName("edited_date")
    Date getEditedDate();
    
    @PropertyName("is_public")
    boolean isPublic();
    
    @PropertyName("integrator_email")
    String getIntegratorEmail();
    
    boolean isDisabled();

    boolean isDeleted();

    @PropertyName("pipeline_eligibility")
    PipelineEligibility getPipelineEligibility();

    @PropertyName("is_favorite")
    void setFavorite(boolean favorite);

    @PropertyName("wiki_url")
    void setWikiUrl(String wikiUrl);

    @PropertyName("integrator_name")
    void setIntegratorName(String integratorName);

    @PropertyName("integration_date")
    void setIntegrationDate(Date integrationDate);

    @PropertyName("edited_date")
    void setEditedDate(Date editedDate);

    @PropertyName("is_public")
    void setPublic(boolean isPublic);

    @PropertyName("integrator_email")
    void setIntegratorEmail(String integratorEmail);

    void setId(String id);

    void setDisabled(boolean disabled);

    void setDeleted(boolean deleted);

    @PropertyName("pipeline_eligibility")
    void setPipelineEligibility(PipelineEligibility pipelineEligibility);

    @PropertyName("step_count")
    int getStepCount();

    @PropertyName("step_count")
    void setStepCount(int step_count);

    @PropertyName("can_run")
    boolean isRunnable();

    @PropertyName("can_run")
    void setRunnable(boolean can_run);

    List<String> getReferences();

    void setReferences(List<String> references);

    @PropertyName("components")
    List<DeployedComponent> getDeployedComponents();

    List<AppDataObject> getInputs();

    void setInputs(List<AppDataObject> inputs);

    List<AppDataObject> getOutputs();

    void setOutputs(List<AppDataObject> outputs);

    List<AppGroup> getGroups();

    @PropertyName("suggested_groups")
    List<AppGroup> getSuggestedGroups();
}
