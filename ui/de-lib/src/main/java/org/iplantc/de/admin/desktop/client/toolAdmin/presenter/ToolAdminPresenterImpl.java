package org.iplantc.de.admin.desktop.client.toolAdmin.presenter;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.service.ToolAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolPublicAppListWindow;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.models.errorHandling.ServiceErrorCode;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolList;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

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

    private void updateTool(final Tool tool, final boolean overwrite) {
        toolAdminServiceFacade.updateTool(tool, overwrite, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                SimpleServiceError serviceError =
                        AutoBeanCodex.decode(factory, SimpleServiceError.class, caught.getMessage())
                                     .as();
                if (serviceError.getErrorCode().equals(ServiceErrorCode.ERR_NOT_WRITEABLE.toString())) {
                    IPlantDialog overwriteDialog = getPublicAppDialog(caught,
                                                                      appearance.confirmOverwriteTitle(),
                                                                      appearance.confirmOverwriteDangerZone(),
                                                                      appearance.confirmOverwriteBody());
                    overwriteDialog.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
                        @Override
                        public void onSelect(SelectEvent event) {
                            updateTool(tool, true);
                        }
                    });
                    overwriteDialog.show();
                } else {
                    ErrorHandler.post(caught);
                }
            }

            @Override
            public void onSuccess(Void result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.updateToolSuccessText()));
                updateView();
            }
        });
    }

    private IPlantDialog getPublicAppDialog(Throwable caught, String title, String before, String after) {
        AppAutoBeanFactory
                appAutoBeanFactory = GWT.create(AppAutoBeanFactory.class);
        AppList appList = AutoBeanCodex.decode(appAutoBeanFactory, AppList.class, caught.getMessage()).as();
        ToolPublicAppListWindow publicAppListWindow = new ToolPublicAppListWindow();
        publicAppListWindow.addApps(appList.getApps());
        IPlantDialog publicAppDialog = getIplantDialogWindow(title);
        publicAppDialog.setMinHeight(200);
        HTML bodyBeforeApps = new HTML();
        bodyBeforeApps.setHTML(before);
        HTML bodyAfterApps = new HTML();
        bodyAfterApps.setHTML(after);

        FlowLayoutContainer container = addScrollSupport();
        container.add(bodyBeforeApps);
        container.add(publicAppListWindow);
        container.add(bodyAfterApps);
        publicAppDialog.add(container);

        return publicAppDialog;
    }

    private IPlantDialog getIplantDialogWindow(String title) {
        final IPlantDialog dialogWindow = new IPlantDialog();
        dialogWindow.setHeadingText(title);
        dialogWindow.setResizable(true);
        return dialogWindow;
    }

    private FlowLayoutContainer addScrollSupport() {
        FlowLayoutContainer container = new FlowLayoutContainer();
        container.getScrollSupport().setScrollMode(ScrollSupport.ScrollMode.AUTO);
        return container;
    }

    @Override
    public void updateTool(Tool tool) {
        updateTool(tool, false);
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
                SimpleServiceError serviceError =
                        AutoBeanCodex.decode(factory, SimpleServiceError.class, caught.getMessage())
                                     .as();
                if (serviceError.getErrorCode().equals(ServiceErrorCode.ERR_NOT_WRITEABLE.toString())) {
                    IPlantDialog publicAppDialog = getPublicAppDialog(caught,
                                                                      appearance.deletePublicToolTitle(),
                                                                      appearance.deletePublicToolBody(),
                                                                      null);

                    publicAppDialog.show();
                } else {
                    ErrorHandler.post(caught);
                }
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
