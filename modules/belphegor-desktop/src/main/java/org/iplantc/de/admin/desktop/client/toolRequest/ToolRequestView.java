package org.iplantc.de.admin.desktop.client.toolRequest;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;
import org.iplantc.de.client.models.toolRequest.ToolRequestUpdate;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface ToolRequestView extends IsWidget, IsMaskable {

    public interface Presenter {

        /**
         * Submits the given update to the {@link org.iplantc.de.admin.desktop.client.toolRequest.service.ToolRequestServiceFacade#updateToolRequest} endpoint.
         * Upon success, the presenter will refresh the view.
         * 
         * @param update
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

    }

    void setPresenter(Presenter presenter);

    void setToolRequests(List<ToolRequest> toolRequests);

    void maskDetailsPanel(String loadingMask);

    void unmaskDetailsPanel();

    void setDetailsPanel(ToolRequestDetails result);

    void update(ToolRequestUpdate toolRequestUpdate, ToolRequestDetails toolRequestDetails);

}
