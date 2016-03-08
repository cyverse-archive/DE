package org.iplantc.de.admin.desktop.client.toolRequest;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;
import org.iplantc.de.client.models.toolRequest.ToolRequestUpdate;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author jstroot
 */
public interface ToolRequestView extends IsWidget, IsMaskable {

    public interface ToolRequestViewAppearance {

        String dateSubmittedColumnLabel();

        int dateSubmittedColumnWidth();

        String dateUpdatedColumnLabel();

        int dateUpdatedColumnWidth();

        String nameColumnLabel();

        int nameColumnWidth();

        String statusColumnLabel();

        int statusColumnWidth();

        String submitBtnText();

        String updateBtnText();

        ImageResource updateIcon();

        double eastPanelSize();

        double northPanelSize();

        String updateToolRequestDlgHeading();

        String updateToolRequestDlgHeight();

        String updateToolRequestDlgWidth();

        String updatedByColumnLabel();

        int updatedByColumnWidth();

        String versionColumnLabel();

        int versionColumnWidth();

        String currentStatusLabel();

        String setStatusLabel();

        String setArbitraryStatusLabel();

        String commentsLabel();

        String detailsPanelHeading();

        String additionalDataFileLabel();

        String additionalInfoLabel();

        String architectureLabel();

        String attributionLabel();

        String cmdLineDescriptionLabel();

        String documentationUrlLabel();

        String multiThreadedLabel();

        String phoneLabel();

        String sourceUrlLabel();

        String submittedByLabel();

        String testDataPathLabel();

        String versionLabel();
    }

    public interface Presenter {

        public interface ToolRequestPresenterAppearance {

            String getToolRequestDetailsLoadingMask();

            String getToolRequestsLoadingMask();

            String toolRequestUpdateSuccessMessage();
        }

        /**
         * Submits the given update to the {@link org.iplantc.de.admin.desktop.client.toolRequest.service.ToolRequestServiceFacade#updateToolRequest} endpoint.
         * Upon success, the presenter will refresh the view.
         * 
         */
        void updateToolRequest(String id, ToolRequestUpdate update);

        /**
         * Retrieves and assembles a {@link ToolRequestDetails} object via the
         * {@link org.iplantc.de.admin.desktop.client.toolRequest.service.ToolRequestServiceFacade#getToolRequestDetails} endpoint.
         * 
         * Upon success, the presenter will refresh the view.
         * 
         */
        void fetchToolRequestDetails(ToolRequest toolRequest);

        void go(HasOneWidget container);

        void setViewDebugId(String baseId);

    }

    void setPresenter(Presenter presenter);

    void setToolRequests(List<ToolRequest> toolRequests);

    void maskDetailsPanel(String loadingMask);

    void unmaskDetailsPanel();

    void setDetailsPanel(ToolRequestDetails result);

    void update(ToolRequestUpdate toolRequestUpdate, ToolRequestDetails toolRequestDetails);

}
