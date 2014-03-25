
/**
 * 
 * 
 */
package org.iplantc.de.client.models.sharing;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.diskResources.DiskResource.PermissionValue;
import org.iplantc.de.client.util.DiskResourceUtil;

/**
 * 
 * @author sriram
 * 
 */
public class DataSharing extends Sharing {

    private String path;
    private String displayPermission;
    private PermissionValue permission;

    public static enum TYPE {
        FILE, FOLDER
    };

    public DataSharing(Collaborator c, PermissionValue p, String path) {
        super(c);
        setPath(path);
        if (p != null) {
            permission = p;
            if (isOwner()) {
                setDisplayPermission(PermissionValue.own.toString());
            } else if (isWritable()) {
                setDisplayPermission(PermissionValue.write.toString());
            } else {
                setDisplayPermission(PermissionValue.read.toString());
            }
        }

    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResourceName() {
        return DiskResourceUtil.parseNameFromPath(path);
    }

    public boolean isReadable() {
        return permission != null && (permission.equals(PermissionValue.read) || permission.equals(PermissionValue.write) || permission.equals(PermissionValue.own));
    }

    public boolean isWritable() {
        return permission != null && (permission.equals(PermissionValue.own) || permission.equals(PermissionValue.write));
    }

    public boolean isOwner() {
        return permission != null && permission.equals(PermissionValue.own);
    }

    public String getPath() {
        return path;
    }

    @Override
    public String getKey() {
        return super.getKey() + getPath();
    }

    public void setDisplayPermission(String perm) {
        displayPermission = perm;
    }

    public String getDisplayPermission() {
        return displayPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof DataSharing)) {
            return false;
        }
        DataSharing s = (DataSharing)o;
        return getKey().equals(s.getKey()) && s.getDisplayPermission().equals(getDisplayPermission());
    }

    @Override
    public DataSharing copy() {
        Collaborator c = getCollaborator();
        String path = getPath();
        return new DataSharing(c, permission, path);
    }

}