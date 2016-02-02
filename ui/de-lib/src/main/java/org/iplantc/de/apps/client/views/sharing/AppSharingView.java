/**
 * 
 * @author sriram
 */

package org.iplantc.de.apps.client.views.sharing;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.sharing.SharingPresenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public interface AppSharingView extends IsWidget {

    void addShareWidget(Widget widget);

    void setPresenter(SharingPresenter sharingPresenter);

    void setSelectedApps(List<App> models);
}
