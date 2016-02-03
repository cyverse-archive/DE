package org.iplantc.de.client.models.sharing;

/**
 * Created by sriram on 2/3/16.
 */
public interface UserPermission {

    /**
     * @return user name
     */
    String getUser();

    void setUser(String user);

    /**
     * @return permission for the user
     */
    String getPermission();

    void setPermission(String permission);

}
