package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.diskResources.DiskResource.PermissionValue;

import com.google.web.bindery.autobean.shared.AutoBean;

public class DiskResourcePermissionCategory {

    public static boolean owner(AutoBean<DiskResource> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && permission.equals(PermissionValue.own);
    }

    public static boolean readable(AutoBean<DiskResource> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && (permission.equals(PermissionValue.read) || permission.equals(PermissionValue.write) || permission.equals(PermissionValue.own));
    }

    public static boolean writable(AutoBean<DiskResource> instance) {
        PermissionValue permission = instance.as().getPermission();
        return permission != null && (permission.equals(PermissionValue.own) || permission.equals(PermissionValue.write));
    }
}
