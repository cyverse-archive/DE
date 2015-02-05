package org.iplantc.de.apps.integration.client.view.propertyEditors;

import org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids;
import org.iplantc.de.apps.integration.shared.AppIntegrationModule.PropertyPanelIds;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToStringConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.DataSource;
import org.iplantc.de.client.models.apps.integration.FileInfoType;
import org.iplantc.de.client.services.AppBuilderMetadataServiceFacade;
import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.FileOutputLabels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class FileOutputPropertyEditor extends AbstractArgumentPropertyEditor {

    interface EditorDriver extends SimpleBeanEditorDriver<Argument, FileOutputPropertyEditor> {
    }

    interface FileOutputPropertyEditorUiBinder extends UiBinder<Widget, FileOutputPropertyEditor> {
    }

    @UiField(provided = true) AppsWidgetsPropertyPanelLabels appLabels;
    @UiField @Path("name") TextField argumentOptionEditor;

    @UiField(provided = true)
    @Ignore // FIXME Why is this ignored but still has a path annotation?
    @Path("fileParameters.dataSource")
    ComboBox<DataSource> dataSourceComboBox;

    @UiField FieldLabel dataSourceLabel, toolTipLabel, argumentOptionLabel, defaultValueLabel;
    @UiField(provided = true) ArgumentEditorConverter<String> defaultValueEditor;
    @UiField @Path("visible") CheckBoxAdapter doNotDisplay;

    @UiField(provided = true)
    @Ignore // FIXME Why is this ignored but still has a path annotation?
    @Path("fileParameters.fileInfoType")
    ComboBox<FileInfoType> fileInfoTypeComboBox;

    @UiField(provided = true) FileOutputLabels fileOutputLabels;
    @UiField TextField label;
    @UiField CheckBoxAdapter omitIfBlank, requiredEditor;
    @UiField @Path("fileParameters.implicit") CheckBoxAdapter isImplicit;
    @UiField @Path("description") TextField toolTipEditor;

    private static FileOutputPropertyEditorUiBinder uiBinder = GWT.create(FileOutputPropertyEditorUiBinder.class);
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public FileOutputPropertyEditor(final AppTemplateWizardAppearance appearance,
                                    final AppsWidgetsPropertyPanelLabels appLabels,
                                    final AppsWidgetsContextualHelpMessages help,
                                    final AppBuilderMetadataServiceFacade appMetadataService,
                                    final IplantValidationConstants validationConstants) {
        super(appearance);
        this.appLabels = appLabels;
        this.fileOutputLabels = appLabels;

        TextField textField = new TextField();
        textField.addValidator(new DiskResourceNameValidator());
        textField.setEmptyText(fileOutputLabels.fileOutputEmptyText());
        defaultValueEditor = new ArgumentEditorConverter<>(textField, new SplittableToStringConverter());
        fileInfoTypeComboBox = createFileInfoTypeComboBox(appMetadataService);
        dataSourceComboBox = createDataSourceComboBox(appMetadataService);

        initWidget(uiBinder.createAndBindUi(this));

        argumentOptionEditor.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));

        defaultValueLabel.setHTML(appearance.createContextualHelpLabel(fileOutputLabels.fileOutputDefaultLabel(), help.fileOutputDefaultValue()));
        toolTipLabel.setHTML(appearance.createContextualHelpLabel(appLabels.toolTipText(), help.toolTip()));
        argumentOptionLabel.setHTML(appearance.createContextualHelpLabel(appLabels.argumentOption(), help.argumentOption()));
        doNotDisplay.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.doNotDisplay()).toSafeHtml());

        requiredEditor.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.isRequired()).toSafeHtml());
        dataSourceLabel.setHTML(appearance.createContextualHelpLabel(appLabels.fileOutputSourceLabel(), help.fileOutputOutputSource()));
        omitIfBlank.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;")
                                                 .append(appearance.createContextualHelpLabelNoFloat(appLabels.excludeWhenEmpty(), help.fileOutputExcludeArgument()))
                                                 .toSafeHtml());
        isImplicit.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appearance.createContextualHelpLabelNoFloat(appLabels.doNotPass(), help.doNotPass()))
                                                .toSafeHtml());


        editorDriver.initialize(this);
        editorDriver.accept(new InitializeTwoWayBinding(this));
        ensureDebugId(Ids.PROPERTY_EDITOR + Ids.FILE_OUTPUT);
    }

    @Override
    public void edit(Argument argument) {
        super.edit(argument);
        editorDriver.edit(argument);
    }

    @Override
    public com.google.gwt.editor.client.EditorDriver<Argument> getEditorDriver() {
        return editorDriver;
    }

    @Override
    @Ignore
    protected ComboBox<DataSource> getDataSourceComboBox() {
        return dataSourceComboBox;
    }

    @Override
    @Ignore
    protected LeafValueEditor<Splittable> getDefaultValueEditor() {
        return defaultValueEditor;
    }

    @Override
    @Ignore
    protected ComboBox<FileInfoType> getFileInfoTypeComboBox() {
        return fileInfoTypeComboBox;
    }

    @Override
    protected void initLabelOnlyEditMode(boolean isLabelOnlyEditMode) {
        dataSourceComboBox.setEnabled(!isLabelOnlyEditMode);
        defaultValueEditor.setEnabled(!isLabelOnlyEditMode);
        doNotDisplay.setEnabled(!isLabelOnlyEditMode);
        fileInfoTypeComboBox.setEnabled(!isLabelOnlyEditMode);
        isImplicit.setEnabled(!isLabelOnlyEditMode);
        argumentOptionEditor.setEnabled(!isLabelOnlyEditMode);
        omitIfBlank.setEnabled(!isLabelOnlyEditMode);
        requiredEditor.setEnabled(!isLabelOnlyEditMode);

        if (isLabelOnlyEditMode) {
            dataSourceComboBox.getValidators().clear();
            defaultValueEditor.getValidators().clear();
            doNotDisplay.getValidators().clear();
            fileInfoTypeComboBox.getValidators().clear();
            isImplicit.getValidators().clear();
            argumentOptionEditor.getValidators().clear();
            omitIfBlank.getValidators().clear();
            requiredEditor.getValidators().clear();
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        label.ensureDebugId(baseID + PropertyPanelIds.LABEL);
        argumentOptionEditor.ensureDebugId(baseID + PropertyPanelIds.ARGUMENT_OPTION);
        requiredEditor.ensureDebugId(baseID + PropertyPanelIds.REQUIRED);
        omitIfBlank.ensureDebugId(baseID + PropertyPanelIds.OMIT_IF_BLANK);
        toolTipEditor.ensureDebugId(baseID + PropertyPanelIds.TOOL_TIP);
        dataSourceComboBox.ensureDebugId(baseID + PropertyPanelIds.DATA_SOURCE);
        fileInfoTypeComboBox.ensureDebugId(baseID + PropertyPanelIds.FILE_INFO_TYPE);
    }

    @UiHandler("defaultValueEditor")
    void onDefaultValueChange(ValueChangeEvent<Splittable> event) {
        // Forward defaultValue onto value.
        model.setValue(event.getValue());
    }
}
