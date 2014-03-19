/**
 * 
 */
package org.iplantc.de.client.models.collaborators;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author sriram
 * 
 */
public interface Collaborator {

    @PropertyName("id")
    public void setId(String id);

    @PropertyName("id")
    public String getId();

    @PropertyName("username")
    public void setUserName(String username);

    @PropertyName("username")
    public String getUserName();

    @PropertyName("firstname")
    public void setFirstName(String firstname);

    @PropertyName("firstname")
    public String getFirstName();

    @PropertyName("lastname")
    public void setLastName(String lastname);

    @PropertyName("lastname")
    public String getLastName();

    @PropertyName("email")
    public void setEmail(String email);

    @PropertyName("email")
    public String getEmail();

    @PropertyName("institution")
    public String getInstitution();

    @PropertyName("institution")
    public void setInstitution(String ins);
}
