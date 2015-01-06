package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;
import java.util.List;

public interface App extends HasId, HasName, HasDescription {

    @PropertyName("app_type")
    String getAppType();

    @PropertyName("app_type")
    void setAppType(String appType);

    @PropertyName("is_favorite")
    boolean isFavorite();

    @PropertyName("wiki_url")
    String getWikiUrl();
    
    @PropertyName("integrator_name")
    String getIntegratorName();
    
    AppFeedback getRating();
    
    void setRating(AppFeedback fb);

    @PropertyName("integration_date")
    Date getIntegrationDate();
    
    @PropertyName("edited_date")
    Date getEditedDate();
    
    @PropertyName("is_public")
    Boolean isPublic();
    
    @PropertyName("integrator_email")
    String getIntegratorEmail();
    
    Boolean isDisabled();

    Boolean isDeleted();

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

    void setDisabled(Boolean disabled);

    void setDeleted(Boolean deleted);

    @PropertyName("pipeline_eligibility")
    void setPipelineEligibility(PipelineEligibility pipelineEligibility);

    @PropertyName("step_count")
    Integer getStepCount();

    @PropertyName("step_count")
    void setStepCount(Integer step_count);

    @PropertyName("can_run")
    Boolean isRunnable();

    @PropertyName("can_run")
    void setRunnable(Boolean can_run);

    List<String> getReferences();

    void setReferences(List<String> references);

    @PropertyName("tools")
    List<Tool> getTools();

    List<AppFileParameters> getInputs();

    void setInputs(List<AppFileParameters> inputs);

    List<AppFileParameters> getOutputs();

    void setOutputs(List<AppFileParameters> outputs);

    @PropertyName("categories")
    List<AppCategory> getGroups();

    @PropertyName("suggested_groups")
    List<AppCategory> getSuggestedGroups();
}
