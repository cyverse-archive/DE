package org.iplantc.de.admin.desktop.client.toolAdmin.presenter;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.service.ToolAdminServiceFacade;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolList;
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
    public void addTool(Tool tool) {

        //The UI handles creating a single tool request, but the admin/tools POST endpoint requires
        // an array of requests.  Wrapping the request inside an array.
        ToolList toolListAutoBean = factory.getToolList().as();
        List<Tool> toolList = new ArrayList<Tool>();
        toolList.add(tool);
        toolListAutoBean.setToolList(toolList);

        toolAdminServiceFacade.addTool(toolListAutoBean, new AsyncCallback<Void>() {

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
    public void updateTool(Tool tool) {
        toolAdminServiceFacade.updateTool(tool, new AsyncCallback<Void>() {
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
        toolAdminServiceFacade.getToolDetails(tool.getId(), new AsyncCallback<Tool>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(Tool result) {
                view.setToolDetails(result);
            }
        });
    }

    @Override
    public void deleteTool(final Tool tool) {
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
