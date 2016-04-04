package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestDetails;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * 
 * 
 * @author sriram
 * 
 */

public interface PermanentIdRequestView extends IsWidget, IsMaskable {

    void setPresenter(Presenter p);

    void loadRequests(List<PermanentIdRequest> requests);

    void update(PermanentIdRequest request);

    void fetchMetadata(Folder selectedFolder,
                       PermanentIdRequestPresenterAppearance appearance,
                       DiskResourceServiceFacade drsvc);

    public interface PermanentIdRequestViewAppearance {
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

        String request();

        String userEmail();

        String folderNotFound();
    }

    public interface Presenter {

        void fetchMetadata();

        void go(HasOneWidget container);

        void getPermIdRequests();

        void loadPermIdRequests();

        void setSelectedRequest(PermanentIdRequest request);

        void doUpdateRequest(PermanentIdRequestUpdate update);

        void onUpdateRequest();

        void createPermanentId();

        void getRequestDetails(AsyncCallback<PermanentIdRequestDetails> callback);

        void setViewDebugId(String baseId);
    }

    public interface PermanentIdRequestPresenterAppearance {
        String createPermIdSucess();

        String createPermIdFailure();

        String folderNotFound(String path);

        String metadataSaveError();

        String requestLoadFailure();

        String statusUpdateFailure();

        String statusUpdateSuccess();

        String updateStatus();

        String update();
    }

}
