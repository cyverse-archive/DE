/**
 *
 *
 */
package org.iplantc.de.client.models.sharing;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.diskResources.PermissionValue;

/**
 * @author sriram
 */
public class Sharing {

    private String id;
    private PermissionValue displayPermission;
    private PermissionValue permission;
    private final Collaborator collaborator;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public Sharing(final Collaborator c, final PermissionValue p, final String id, final String name) {
        this.collaborator = c;
        setId(id);
        setName(name);
        if (p != null) {
            permission = p;
            displayPermission = permission;
        }

    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isReadable() {
        return permission != null && (permission.equals(PermissionValue.read) || permission.equals(
                PermissionValue.write) || permission.equals(PermissionValue.own));
    }

    public boolean isWritable() {
        return permission != null && (permission.equals(PermissionValue.own) || permission.equals(
                PermissionValue.write));
    }

    public boolean isOwner() {
        return permission != null && permission.equals(PermissionValue.own);
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return getCollaborator().getUserName() + getId();
    }

    public void setPermission(PermissionValue perm) {
        this.permission = perm;
    }

    public PermissionValue getPermission() {
        return permission;
    }

    public void setDisplayPermission(PermissionValue perm) {
        displayPermission = perm;
    }

    public PermissionValue getDisplayPermission() {
        return displayPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Sharing)) {
            return false;
        }
        Sharing s = (Sharing)o;
        return getKey().equals(s.getKey()) && s.getDisplayPermission().equals(getDisplayPermission());
    }

    public Sharing copy() {
        return new Sharing(getCollaborator(), permission, id, name);
    }

    public String getUserName() {
        return getCollaborator().getUserName();
    }

    public String getName() {
        return name;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }


    public String getCollaboratorName() {
        StringBuilder builder = new StringBuilder();
        if (getCollaborator().getFirstName() != null && !getCollaborator().getFirstName().isEmpty()) {
            builder.append(getCollaborator().getFirstName());
            if (getCollaborator().getLastName() != null && !getCollaborator().getLastName().isEmpty()) {
                builder.append(" " + getCollaborator().getLastName());
            }
            return builder.toString();
        } else {
            return getCollaborator().getUserName();
        }
    }

}