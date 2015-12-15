package org.iplantc.de.admin.desktop.client.permIdRequest.view;

import org.iplantc.de.client.models.IsMaskable;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 * 
 * @author sriram
 * 
 */

public interface PermIdRequestView extends IsWidget, IsMaskable {

    public interface PermIdRequestViewAppearance {
        String dateSubmittedColumnLabel();

        int dateSubmittedColumnWidth();

        String dateUpdatedColumnLabel();

        int dateUpdatedColumnWidth();

        String nameColumnLabel();

        int nameColumnWidth();

        String submitBtnText();

        String updateBtnText();

        ImageResource updateIcon();

        String pathColumnLabel();

        int pathColumnWidth();

        int northPanelSize();

        int eastPanelSize();

    }

    public interface Presenter {

        void fetchMetadata();

        void go(HasOneWidget container);

        void getPermIdRequests();

        void loadPermIdRequests();
    }

}
