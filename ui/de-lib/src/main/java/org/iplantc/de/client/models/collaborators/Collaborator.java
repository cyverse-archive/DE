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

    @PropertyName("username")
    void setUserName(String username);

    @PropertyName("username")
    String getUserName();

    @PropertyName("firstname")
    void setFirstName(String firstname);

    @PropertyName("firstname")
    String getFirstName();

    @PropertyName("lastname")
    void setLastName(String lastname);

    @PropertyName("lastname")
    String getLastName();

    @PropertyName("name")
    void setName(String name);

    @PropertyName("name")
    String getName();

    @PropertyName("email")
    void setEmail(String email);

    @PropertyName("email")
    String getEmail();

    @PropertyName("institution")
    String getInstitution();

    @PropertyName("institution")
    void setInstitution(String ins);
}
