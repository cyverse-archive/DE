package org.iplantc.de.client.models.apps.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 2/3/16.
 */
public interface AppSharingRequestList {

    @AutoBean.PropertyName("sharing")
    List<AppSharingRequest> getAppSharingRequestList();

    @AutoBean.PropertyName("sharing")
    void setAppSharingRequestList(List<AppSharingRequest> sharinglist);


}
