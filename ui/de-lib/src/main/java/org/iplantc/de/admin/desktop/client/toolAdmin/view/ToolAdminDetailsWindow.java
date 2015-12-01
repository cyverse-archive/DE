package org.iplantc.de.admin.desktop.client.toolAdmin.view;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolContainerEditor;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews.ToolImplementationEditor;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolImportUpdateRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Created by aramsey on 10/30/15.
 */
public class ToolAdminDetailsWindow extends Composite implements Editor<ToolImportUpdateRequest> {


    interface EditorDriver
            extends SimpleBeanEditorDriver<ToolImportUpdateRequest, ToolAdminDetailsWindow> {
    }

    interface ToolAdminDetailsWindowUiBinder extends UiBinder<Widget, ToolAdminDetailsWindow> {

    }

    private ToolAutoBeanFactory factory;
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private static ToolAdminDetailsWindowUiBinder uiBinder =
            GWT.create(ToolAdminDetailsWindowUiBinder.class);

    @UiField VerticalLayoutContainer layoutContainer;
    @Ignore
    @UiField FieldLabel nameLabel, typeLabel, locationLabel;
    @UiField TextArea descriptionEditor;
    @UiField TextField nameEditor;
    @UiField TextField typeEditor;
    @UiField TextField attributionEditor;
    @UiField TextField versionEditor;
    @UiField TextField locationEditor;
    @UiField ToolImplementationEditor implementationEditor;
    @UiField ToolContainerEditor containerEditor;
    @UiField (provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance = GWT.create(ToolAdminView.ToolAdminViewAppearance.class);


    private ToolAdminDetailsWindow(ToolAutoBeanFactory toolFactory) {
        this.factory = toolFactory;
        ToolImportUpdateRequest request = factory.getRequest().as();
        initWidget(uiBinder.createAndBindUi(this));

        nameLabel.setHTML(appearance.toolImportNameLabel());
        typeLabel.setHTML(appearance.toolImportTypeLabel());
        locationLabel.setHTML(appearance.toolImportLocationLabel());

        descriptionEditor.setHeight(250);
        request.setType(appearance.toolImportTypeDefaultValue());
        request.setContainer(containerEditor.getToolContainer());

        editorDriver.initialize(this);
        editorDriver.edit(request);
    }

    public static ToolAdminDetailsWindow addToolDetails(ToolAutoBeanFactory factory) {
        return new ToolAdminDetailsWindow(factory);
    }

    public void edit(ToolImportUpdateRequest tool) {
        editorDriver.edit(tool);
    }

    public ToolImportUpdateRequest getToolImportUpdateRequest() {

        ToolImportUpdateRequest request = editorDriver.flush();
        request.setContainer(containerEditor.getToolContainer());
        request.setImplementation(implementationEditor.getToolImplementation());

        return request;
    }

    public boolean isValid() {
        return containerEditor.isValid() && implementationEditor.isValid() && nameEditor.isValid()
               && typeEditor.isValid() && locationEditor.isValid();
    }
}
