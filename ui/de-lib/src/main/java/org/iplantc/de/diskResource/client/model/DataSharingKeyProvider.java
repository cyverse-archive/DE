/**
 * 
 */
package org.iplantc.de.diskResource.client.model;

import org.iplantc.de.client.models.sharing.Sharing;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author sriram
 *
 */
public class DataSharingKeyProvider implements ModelKeyProvider<Sharing> {
    @Override
    public String getKey(Sharing item) {
        return item.getKey();
    }
}