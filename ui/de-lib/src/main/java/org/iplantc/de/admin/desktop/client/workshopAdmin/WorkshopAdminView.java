package org.iplantc.de.admin.desktop.client.workshopAdmin;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.iplantc.de.client.models.IsMaskable;

/**
 * @author dennis
 */
public interface WorkshopAdminView extends IsWidget, IsMaskable {

    interface WorkshopAdminViewAppearance {

        String add();

        ImageResource addIcon();

        String delete();

        ImageResource deleteIcon();
    }

    interface Presenter {

        void go(HasOneWidget container);

        void setViewDebugId(String baseId);
    }
}
