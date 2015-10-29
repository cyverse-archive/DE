package org.iplantc.de.admin.desktop.client.toolAdmin.presenter;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.service.ToolAdminServiceFacade;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolImportUpdateRequest;
import org.iplantc.de.client.models.tool.ToolImportUpdateRequestList;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aramsey on 10/27/15.
 */


public class ToolAdminPresenterImpl implements ToolAdminView.Presenter {

    private final ToolAdminView view;
    private final ToolAdminServiceFacade toolAdminServiceFacade;
    private final ToolAutoBeanFactory factory;
    private final ToolAdminView.ToolAdminViewAppearance appearance;

    @Inject
    public ToolAdminPresenterImpl(final ToolAdminView view,
                                  ToolAdminServiceFacade toolAdminServiceFacade,
                                  ToolAutoBeanFactory factory,
                                  ToolAdminView.ToolAdminViewAppearance appearance) {
        this.view = view;
        this.factory = factory;
        this.appearance = appearance;
        view.setPresenter(this);
        this.toolAdminServiceFacade = toolAdminServiceFacade;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
        updateView();

    }

    @Override
    public void addTool(ToolImportUpdateRequest request) {

        //The UI handles creating a single tool request, but the admin/tools POST endpoint requires
        // an array of requests.  Wrapping the request inside an array.
        ToolImportUpdateRequestList requestList = factory.update().as();
        List<ToolImportUpdateRequest> toolRequestList = new ArrayList<ToolImportUpdateRequest>();
        toolRequestList.add(request);
        requestList.setToolImportList(toolRequestList);

        toolAdminServiceFacade.addTool(requestList, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(Void result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.addToolSuccessText()));
                updateView();
            }

        });
    }

    @Override
    public void updateTool(ToolImportUpdateRequest request) {
        toolAdminServiceFacade.updateTool(request, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(Void result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.updateToolSuccessText()));
                updateView();
            }
        });
    }

    @Override
    public void getToolDetails(Tool tool) {
        toolAdminServiceFacade.getToolDetails(tool.getId(), new AsyncCallback<ToolImportUpdateRequest>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(ToolImportUpdateRequest result) {
                view.setToolDetails(result);
            }
        });
    }

    @Override
    public void deleteTool(final ToolImportUpdateRequest tool) {
        toolAdminServiceFacade.deleteTool(tool.getId(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(Void result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.deleteToolSuccessText()));
                view.deleteTool(tool.getId());
            }
        });
    }

    private void updateView() {
        String searchTerm = "*";
        updateView(searchTerm);
    }

    private void updateView(String searchTerm) {
        toolAdminServiceFacade.getTools(searchTerm, new AsyncCallback<List<Tool>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<Tool> result) {
                view.setToolList(result);
            }
        });
    }
}
