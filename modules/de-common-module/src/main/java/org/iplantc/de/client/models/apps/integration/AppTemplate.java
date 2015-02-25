package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasLabel;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;
import java.util.List;

/**
 * This object contains all the information required to assemble a representative App UI wizard.
 * Additionally, it will also be used to hold the user data entered via the wizard forms.
 * 
 * @author jstroot
 * 
 */
public interface AppTemplate extends HasId, HasLabel, HasName, HasDescription {

    String GROUPS_KEY = "groups";

    @PropertyName(GROUPS_KEY)
    List<ArgumentGroup> getArgumentGroups();
    
    @PropertyName(GROUPS_KEY)
    void setArgumentGroups(List<ArgumentGroup> argumentGroups);

    List<Tool> getTools();

    void setTools(List<Tool> tools);

    @PropertyName("edited_date")
    String getEditedDate();

    @PropertyName("edited_date")
    void setEditedDate(String edited_date);

    @PropertyName("integration_date")
    String getPublishedDate();

    @PropertyName("integration_date")
    void setPublishedDate(String published_date);

    void setId(String id);
    
    @PropertyName("disabled")
    public boolean isAppDisabled();

    @PropertyName("disabled")
    public void setAppDisabled(boolean disabled);
    
    @PropertyName("is_public")
    public Boolean isPublic();

    @PropertyName("is_public")
    public void setPublic(Boolean is_public);
}
