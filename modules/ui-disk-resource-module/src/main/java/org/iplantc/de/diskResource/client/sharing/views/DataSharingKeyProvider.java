/**
 * 
 */
package org.iplantc.de.diskResource.client.sharing.views;

import org.iplantc.de.client.models.sharing.DataSharing;

import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * @author sriram
 *
 */
public class DataSharingKeyProvider implements ModelKeyProvider<DataSharing> {
    @Override
    public String getKey(DataSharing item) {
        return item.getKey();
    }
}