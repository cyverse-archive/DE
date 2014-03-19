package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;


/**
 * AutoBean interface for the bootstrap endpoint response.
 * 
 * @author psarando
 * 
 */
public interface UserBootstrap {

    /**
     * Get user's email address.
     * 
     * @return email address.
     */
    public String getEmail();

    /**
     * @return the firstName
     */
    public String getFirstName();

    /**
     * Gets the full username.
     * 
     * @return the fully qualified username.
     */
    @PropertyName("full_username")
    public String getFullUsername();

    /**
     * @return the path to the user's home directory.
     */
    @PropertyName("userHomePath")
    public String getHomePath();

    /**
     * @return the lastName
     */
    public String getLastName();

    public String getLoginTime();

    /**
     * @return the trashPath
     */
    @PropertyName("userTrashPath")
    public String getTrashPath();

    public String getBaseTrashPath();

    /**
     * Gets the username for the user.
     * 
     * This value corresponds to an entry in LDAP.
     * 
     * @return a string representing the username for the user.
     */
    public String getUsername();

    /**
     * Gets the workspace id for the user.
     * 
     * @return a string representing the identifier for workspace.
     */
    public String getWorkspaceId();

    /**
     * @return the newUser
     */
    @PropertyName("newWorkspace")
    public boolean isNewUser();
}
