package org.iplantc.de.admin.desktop.client.toolAdmin.gin;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.gin.factory.ToolAdminViewFactory;
import org.iplantc.de.admin.desktop.client.toolAdmin.presenter.ToolAdminPresenterImpl;
import org.iplantc.de.admin.desktop.client.toolAdmin.service.ToolAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.toolAdmin.service.impl.ToolAdminServiceFacadeImpl;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.ToolAdminDetailsView;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.ToolAdminViewImpl;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs.DeleteToolDialog;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs.OverwriteToolDialog;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs.ToolAdminDetailsDialog;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolContainerEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolDeviceListEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolImageEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolImplementationEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolPublicAppListWindow;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolTestDataEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolTestDataInputFilesListEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolTestDataOutputFilesListEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolVolumeListEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolVolumesFromListEditor;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

/**
 * @author jstroot
 */
public class ToolAdminGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().implement(ToolAdminView.class, ToolAdminViewImpl.class)
                                             .build(ToolAdminViewFactory.class));
        bind(ToolAdminDetailsDialog.class);
        bind(ToolAdminDetailsView.class);
        bind(ToolImplementationEditor.class);
        bind(ToolTestDataEditor.class);
        bind(ToolPublicAppListWindow.class);
        bind(OverwriteToolDialog.class);
        bind(DeleteToolDialog.class);
        bind(ToolTestDataInputFilesListEditor.class);
        bind(ToolTestDataOutputFilesListEditor.class);
        bind(ToolContainerEditor.class);
        bind(ToolDeviceListEditor.class);
        bind(ToolVolumesFromListEditor.class);
        bind(ToolVolumeListEditor.class);
        bind(ToolImageEditor.class);
        bind(ToolAdminView.Presenter.class).to(ToolAdminPresenterImpl.class);
        bind(ToolAdminServiceFacade.class).to(ToolAdminServiceFacadeImpl.class);
    }
}

