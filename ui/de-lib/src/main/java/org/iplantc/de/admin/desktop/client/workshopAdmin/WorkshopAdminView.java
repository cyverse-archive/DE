package org.iplantc.de.admin.desktop.client.workshopAdmin;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.groups.Member;

/**
 * @author dennis
 */
public interface WorkshopAdminView extends IsWidget, IsMaskable {

    interface WorkshopAdminViewAppearance {

        String delete();

        ImageResource deleteIcon();

        int nameColumnWidth();

        String nameColumnLabel();

        int emailColumnWidth();

        String emailColumnLabel();

        int institutionColumnWidth();

        String institutionColumnLabel();
    }

    interface Presenter {

        void go(HasOneWidget container);

        void setViewDebugId(String baseId);
    }

    void memberSelected(Member member);
}
