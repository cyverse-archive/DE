/**
 * 
 */
package org.iplantc.de.diskResource.client.model;

import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.sharing.Sharing;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author sriram
 * 
 */
public interface DataSharingProperties extends PropertyAccess<Sharing> {

    ValueProvider<Sharing, String> name();

    ValueProvider<Sharing, PermissionValue> displayPermission();

}
