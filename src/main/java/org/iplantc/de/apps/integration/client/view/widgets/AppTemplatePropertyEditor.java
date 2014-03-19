package org.iplantc.de.apps.integration.client.view.widgets;

import org.iplantc.de.apps.integration.client.events.UpdateCommandLinePreviewEvent;
import org.iplantc.de.apps.integration.client.events.UpdateCommandLinePreviewEvent.HasUpdateCommandLinePreviewEventHandlers;
import org.iplantc.de.apps.integration.client.events.UpdateCommandLinePreviewEvent.UpdateCommandLinePreviewEventHandler;
import org.iplantc.de.apps.widgets.client.dialog.DCListingDialog;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.AppTemplateSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.HasAppTemplateSelectedEventHandlers;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.ArgumentGroupSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent.ArgumentSelectedEventHandler;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.apps.widgets.client.view.deployedComponents.DCSearchField;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;
import org.iplantc.de.commons.client.validators.AppNameValidator;
import org.iplantc.de.commons.client.widgets.PreventEntryAfterLimitHandler;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

/**
 * @author jstroot
 * 
 */
public class AppTemplatePropertyEditor extends Composite implements ValueAwareEditor<AppTemplate>, HasLabelOnlyEditMode, HasAppTemplateSelectedEventHandlers, ArgumentGroupSelectedEventHandler,
        ArgumentSelectedEventHandler, HasUpdateCommandLinePreviewEventHandlers {

    interface AppTemplatePropertyEditorUiBinder extends UiBinder<Widget, AppTemplatePropertyEditor> {}

    private static AppTemplatePropertyEditorUiBinder BINDER = GWT.create(AppTemplatePropertyEditorUiBinder.class);
    
    @UiField
    AppTemplateContentPanel cp;

    @UiField
    @Path("description")
    TextArea description;

    @UiField
    @Path("name")
    TextField name;

    @Path("name")
    HasHTMLEditor nameEditor;

    @Ignore
    @UiField
    TextButton searchBtn;

    @UiField
    @Ignore
    DCSearchField tool;

    @UiField
    FieldLabel toolLabel, appNameLabel, appDescriptionLabel;

    private final AppTemplateWizardAppearance appearance;

    private boolean labelOnlyEditMode = false;

    private AppTemplate model;

    @Inject
    public AppTemplatePropertyEditor(AppTemplateWizardAppearance appearance) {
        this.appearance = appearance;

        initWidget(BINDER.createAndBindUi(this));
        nameEditor = new HasHTMLEditor(cp.getHeader(), appearance);

        name.addKeyDownHandler(new PreventEntryAfterLimitHandler(name));
        name.addValidator(new MaxLengthValidator(PreventEntryAfterLimitHandler.DEFAULT_LIMIT));
        name.addValidator(new AppNameValidator());
        description.addValidator(new MaxLengthValidator(PreventEntryAfterLimitHandler.DEFAULT_LIMIT));
        description.addKeyDownHandler(new PreventEntryAfterLimitHandler(description));

        initLabels();
    }

    @Override
    public HandlerRegistration addAppTemplateSelectedEventHandler(AppTemplateSelectedEventHandler handler) {
        return cp.addAppTemplateSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addUpdateCommandLinePreviewEventHandler(UpdateCommandLinePreviewEventHandler handler) {
        return addHandler(handler, UpdateCommandLinePreviewEvent.TYPE);
    }

    @Override
    public void flush() {
        if(model == null) {
            return;
        }
        
        model.setDeployedComponent(tool.getValue());
    }

    @Override
    public boolean isLabelOnlyEditMode() {
        return labelOnlyEditMode;
    }

    @Override
    public void onArgumentGroupSelected(ArgumentGroupSelectedEvent event) {
        cp.getHeader().removeStyleName(appearance.getStyle().appHeaderSelect());
    }

    @Override
    public void onArgumentSelected(ArgumentSelectedEvent event) {
        cp.getHeader().removeStyleName(appearance.getStyle().appHeaderSelect());
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public void setDelegate(EditorDelegate<AppTemplate> delegate) {/* Do Nothing */}

    @Override
    public void setLabelOnlyEditMode(boolean labelOnlyEditMode) {
        this.labelOnlyEditMode = labelOnlyEditMode;
        toolLabel.setEnabled(!labelOnlyEditMode);
        appNameLabel.setEnabled(!labelOnlyEditMode);
    }

    @Override
    public void setValue(AppTemplate value) {
        if (value == null) {
            return;
        }
        
        this.model = value;

        if (value.getDeployedComponent() != null) {
            tool.setValue(value.getDeployedComponent());
        } else {
            tool.clear();
        }
    }

    /**
     * @param event
     */
    @UiHandler("name")
    void onNameChanged(ValueChangeEvent<String> event) {
        nameEditor.setValue(event.getValue());
    }

    /**
     * @param event
     */
    @UiHandler("searchBtn")
    void onSearchBtnClick(SelectEvent event) {
        final DCListingDialog dialog = new DCListingDialog();
        dialog.addHideHandler(new HideHandler() {

            @Override
            public void onHide(HideEvent event) {
                DeployedComponent dc = dialog.getSelectedComponent();
                // Set the deployed component in the AppTemplate
                if (dc != null) {
                    tool.setValue(dc);
                    // presenter.onArgumentPropertyValueChange();
                }
            }
        });
        dialog.show();
    }

    /**
     * @param event
     */
    @UiHandler("tool")
    void onToolValueChanged(ValueChangeEvent<DeployedComponent> event) {
        // presenter.onArgumentPropertyValueChange();
        fireEvent(new UpdateCommandLinePreviewEvent());
    }

    private void initLabels() {
        AppsWidgetsPropertyPanelLabels labels = appearance.getPropertyPanelLabels();
        String requiredHtml = appearance.getTemplates().fieldLabelRequired().asString();

        String toolHelp = appearance.getContextHelpMessages().appToolUsed();
        SafeHtml toolLabelHtml = appearance.createContextualHelpLabel(labels.toolUsedLabel(), toolHelp);
        toolLabel.setHTML(toolLabelHtml.asString());
        new QuickTip(toolLabel).getToolTipConfig().setDismissDelay(0);

        appNameLabel.setHTML(requiredHtml + labels.appNameLabel());
        appDescriptionLabel.setHTML(requiredHtml + labels.appDescriptionLabel());
    }
}
