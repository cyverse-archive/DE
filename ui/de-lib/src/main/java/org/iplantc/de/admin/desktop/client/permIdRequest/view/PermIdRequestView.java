package org.iplantc.de.admin.desktop.client.permIdRequest.view;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * 
 * 
 * @author sriram
 * 
 */

public interface PermIdRequestView extends IsWidget, IsMaskable {

    void setPresenter(Presenter p);

    void loadRequests(List<PermanentIdRequest> requests);

    void update(PermanentIdRequest request);

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

        String noRows();

        String confirmCreate(String type);

        String createBtnText();

        String currentStatusLbl();

        String setStatusLbl();

        String commentsLbl();

        String updateStatus();

        String update();

        String request();
    }

    public interface Presenter {

        void fetchMetadata();

        void go(HasOneWidget container);

        void getPermIdRequests();

        void loadPermIdRequests();

        void setSelectedRequest(PermanentIdRequest request);

        void updateRequest(PermanentIdRequestUpdate update);

        void setSubmitRequestForId();

        void createPermanentId();
    }

}
