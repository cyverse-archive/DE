package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasLabel;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;

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
    
    @PropertyName("groups")
    List<ArgumentGroup> getArgumentGroups();
    
    @PropertyName("groups")
    void setArgumentGroups(List<ArgumentGroup> argumentGroups);

    DeployedComponent getDeployedComponent();

    void setDeployedComponent(DeployedComponent deployedComponent);

    @PropertyName("edited_date")
    Date getEditedDate();

    @PropertyName("edited_date")
    void setEditedDate(Date edited_date);

    @PropertyName("published_date")
    Date getPublishedDate();

    @PropertyName("published_date")
    void setPublishedDate(Date published_date);

    void setId(String id);
    
    @PropertyName("disabled")
    public boolean isAppDisabled();

    @PropertyName("disabled")
    public void setAppDisabled(boolean disabled);
    
    @PropertyName("is_public")
    public boolean isPublic();

    @PropertyName("is_public")
    public void setPublic(boolean is_public);
}
