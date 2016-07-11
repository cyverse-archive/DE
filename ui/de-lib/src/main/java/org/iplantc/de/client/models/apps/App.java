package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;
import java.util.List;

/**
 * @author jstroot
 */
public interface App extends HasId,
                             HasName,
                             HasDescription {

    String APP_TYPE_KEY = "app_type";
    String CAN_RUN_KEY = "can_run";
    String CATEGORIES_KEY = "categories";
    String EDITED_DATE_KEY = "edited_date";
    String INTEGRATION_DATE_KEY = "integration_date";
    String INTEGRATOR_EMAIL_KEY = "integrator_email";
    String INTEGRATOR_NAME_KEY = "integrator_name";
    String IS_FAVORITE_KEY = "is_favorite";
    String IS_PUBLIC_KEY = "is_public";
    String PIPELINE_ELIGIBILITY_KEY = "pipeline_eligibility";
    String STEP_COUNT_KEY = "step_count";
    String SUGGESTED_GROUPS_KEY = "suggested_groups";
    String TOOLS_KEY = "tools";
    String WIKI_URL_KEY = "wiki_url";
    String REFERENCES_KEY = "references";

    String EXTERNAL_APP = "external";

    @PropertyName(APP_TYPE_KEY)
    String getAppType();

    @PropertyName(EDITED_DATE_KEY)
    Date getEditedDate();

    @PropertyName(CATEGORIES_KEY)
    List<AppCategory> getGroups();

    List<AppFileParameters> getInputs();

    @PropertyName(INTEGRATION_DATE_KEY)
    Date getIntegrationDate();

    @PropertyName(INTEGRATOR_EMAIL_KEY)
    String getIntegratorEmail();

    @PropertyName(INTEGRATOR_NAME_KEY)
    String getIntegratorName();

    List<AppFileParameters> getOutputs();

    @PropertyName(PIPELINE_ELIGIBILITY_KEY)
    PipelineEligibility getPipelineEligibility();

    AppFeedback getRating();

    List<String> getReferences();

    @PropertyName(STEP_COUNT_KEY)
    Integer getStepCount();

    @PropertyName(SUGGESTED_GROUPS_KEY)
    List<AppCategory> getSuggestedGroups();

    @PropertyName(TOOLS_KEY)
    List<Tool> getTools();

    @PropertyName(WIKI_URL_KEY)
    String getWikiUrl();

    Boolean isDeleted();

    Boolean isDisabled();

    @PropertyName(IS_FAVORITE_KEY)
    boolean isFavorite();

    @PropertyName(IS_PUBLIC_KEY)
    Boolean isPublic();

    @PropertyName(CAN_RUN_KEY)
    Boolean isRunnable();

    @PropertyName(APP_TYPE_KEY)
    void setAppType(String appType);

    void setDeleted(Boolean deleted);

    void setDisabled(Boolean disabled);

    @PropertyName(EDITED_DATE_KEY)
    void setEditedDate(Date editedDate);

    @PropertyName(IS_FAVORITE_KEY)
    void setFavorite(boolean favorite);

    void setId(String id);

    void setInputs(List<AppFileParameters> inputs);

    @PropertyName(INTEGRATION_DATE_KEY)
    void setIntegrationDate(Date integrationDate);

    @PropertyName(INTEGRATOR_EMAIL_KEY)
    void setIntegratorEmail(String integratorEmail);

    @PropertyName(INTEGRATOR_NAME_KEY)
    void setIntegratorName(String integratorName);

    void setOutputs(List<AppFileParameters> outputs);

    @PropertyName(PIPELINE_ELIGIBILITY_KEY)
    void setPipelineEligibility(PipelineEligibility pipelineEligibility);

    @PropertyName(IS_PUBLIC_KEY)
    void setPublic(Boolean isPublic);

    void setRating(AppFeedback fb);

    void setReferences(List<String> references);

    @PropertyName(CAN_RUN_KEY)
    void setRunnable(Boolean can_run);

    @PropertyName(STEP_COUNT_KEY)
    void setStepCount(Integer step_count);

    @PropertyName(WIKI_URL_KEY)
    void setWikiUrl(String wikiUrl);

    void setPermission(PermissionValue value);

    PermissionValue getPermission();

    Boolean isBeta();
}
