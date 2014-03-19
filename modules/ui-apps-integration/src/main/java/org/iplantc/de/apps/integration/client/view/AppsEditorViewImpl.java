package org.iplantc.de.apps.integration.client.view;

import org.iplantc.de.apps.integration.client.view.propertyEditors.ArgumentGroupPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.ArgumentPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.DecimalInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.DecimalSelectionPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.EnvVarPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FileInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FileOutputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FlagArgumentPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FolderInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.FolderOutputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.InfoPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.IntegerInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.IntegerSelectionPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.MultiFileInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.MultiFileOutputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.MultiLineTextInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.ReferenceAnnotationPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.ReferenceGenomePropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.ReferenceSequencePropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.TextInputPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.TextSelectionPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.TreeSelectionPropertyEditor;
import org.iplantc.de.apps.integration.client.view.propertyEditors.style.AppTemplateWizardPropertyContentPanelAppearance;
import org.iplantc.de.apps.integration.client.view.widgets.AppTemplatePropertyEditor;
import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentGroupEditor;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.commons.client.widgets.ContextualHelpPopup;
import org.iplantc.de.resources.client.IplantContextualHelpAccessStyle;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

import java.util.Map;

public class AppsEditorViewImpl extends Composite implements AppsEditorView {

    @UiTemplate("AppsEditorView.ui.xml")
    interface AppsIntegrationViewImplUiBinder extends UiBinder<Widget, AppsEditorViewImpl> {}

    private static AppsIntegrationViewImplUiBinder BINDER = GWT.create(AppsIntegrationViewImplUiBinder.class);
    @UiField(provided = true)
    AppTemplatePropertyEditor appTemplatePropertyEditor;

    @Inject
    Provider<ArgumentGroupPropertyEditor> argGrpPropEditorProvider;
    @UiField
    BorderLayoutContainer borderLayoutContainer;
    @Ignore
    @UiField
    TextArea cmdLinePreview;
    @Inject
    Provider<DecimalInputPropertyEditor> decimalInputProvider;
    @Inject
    Provider<DecimalSelectionPropertyEditor> decimalSelectionProvider;
    /**
     * This panel is a card layout so that 'cards' can be created which are bound to
     * an individual Argument.
     */
    @UiField
    CardLayoutContainer eastPanel;
    @Inject
    Provider<EnvVarPropertyEditor> envVarProvider;
    // Inject the Argument Property editor providers.
    @Inject
    Provider<FileInputPropertyEditor> fileInputProvider;
    @Inject
    Provider<FileOutputPropertyEditor> fileOutputProvider;
    @Inject
    Provider<FlagArgumentPropertyEditor> flagProvider;
    @Inject
    Provider<FolderInputPropertyEditor> folderInputProvider;
    @Inject
    Provider<FolderOutputPropertyEditor> folderOutputProvider;
    @Inject
    Provider<InfoPropertyEditor> infoProvider;
    @Inject
    Provider<IntegerInputPropertyEditor> integerInputProvider;
    @Inject
    Provider<IntegerSelectionPropertyEditor> integerSelectionProvider;
    @Inject
    Provider<MultiFileInputPropertyEditor> multiFileInputProvider;
    @Inject
    Provider<MultiFileOutputPropertyEditor> multiFileOutputProvider;
    @Inject
    Provider<MultiLineTextInputPropertyEditor> multiLineInputProvider;
    @UiField
    AppIntegrationPalette palette;
    @Inject
    Provider<ReferenceAnnotationPropertyEditor> refAnnotationProvider;
    @Inject
    Provider<ReferenceGenomePropertyEditor> refGenomeProvider;


    @Inject
    Provider<ReferenceSequencePropertyEditor> refSequenceProvider;

    @Inject
    Provider<TextInputPropertyEditor> textInputProvider;

    @Inject
    Provider<TextSelectionPropertyEditor> textSelectionProvider;

    @UiField(provided = true)
    AppEditorToolbar toolbar;

    @Inject
    Provider<TreeSelectionPropertyEditor> treeSelectionProvider;

    @UiField(provided = true)
    AppTemplateForm wizard;

    private ArgumentGroupPropertyEditor argGrpPropertyEditor;

    private ArgumentPropertyEditor currArgumentPropEditor;
    private final ContentPanel defaultDetailsPanel;
    private final AppsEditorView.EditorDriver editorDriver = GWT.create(AppsEditorView.EditorDriver.class);
    private final AppsWidgetsContextualHelpMessages helpMessages = I18N.APPS_HELP;

    private Presenter presenter;
    private final Map<Object, ArgumentPropertyEditor> propertyEditorMap = Maps.newHashMap();
    private final IplantContextualHelpAccessStyle style = IplantResources.RESOURCES.getContxtualHelpStyle();

    @Inject
    public AppsEditorViewImpl(AppTemplateForm wizard, AppEditorToolbar toolbar, AppTemplatePropertyEditor appTemplatePropertyEditor) {
        this.wizard = wizard;
        this.toolbar = toolbar;
        wizard.setAdjustForScroll(false);
        this.appTemplatePropertyEditor = appTemplatePropertyEditor;
        style.ensureInjected();
        defaultDetailsPanel = new ContentPanel(new AppTemplateWizardPropertyContentPanelAppearance());
        defaultDetailsPanel.setHeadingText(I18N.APPS_LABELS.detailsPanelHeader("")); //$NON-NLS-1$
        defaultDetailsPanel.add(new HTML(I18N.APPS_LABELS.detailsPanelDefaultText()));

        initWidget(BINDER.createAndBindUi(this));
        editorDriver.initialize(this);

        setEastWidget(defaultDetailsPanel);

        /*
         * JDS - Add handling to collapse all argument groups on drag start. To understand why, comment
         * out the handler below, and drag a new argument group to the app wizard. The behaviour is
         * abrasive and jarring to the user.
         */
        palette.grpDragSource.addDragStartHandler(new DndDragStartHandler() {

            @Override
            public void onDragStart(DndDragStartEvent event) {
                if (event.getStatusProxy().getStatus()) {
                    AppsEditorViewImpl.this.wizard.collapseAllArgumentGroups();
                }
            }
        });
    }

    @Override
    public AppTemplate flush() {
        if (currArgumentPropEditor != null) {
            currArgumentPropEditor.getEditorDriver().flush();
        }
        if (argGrpPropertyEditor != null) {
            argGrpPropertyEditor.getEditorDriver().flush();
        }
        AppTemplate flushed = editorDriver.flush();
        return flushed;
    }

    @Override
    public AppTemplateForm getAppTemplateForm() {
        return wizard;
    }

    @Override
    public AppTemplatePropertyEditor getAppTemplatePropertyEditor() {
        return appTemplatePropertyEditor;
    }

    @Override
    public EditorDriver getEditorDriver() {
        return editorDriver;
    }

    @Override
    public AppEditorToolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean hasErrors() {
        boolean argGrpPropertyEditorHasErrors = false;
        boolean currArgPropEditorHasErrors = false;
        if (currArgumentPropEditor != null) {
            currArgPropEditorHasErrors = currArgumentPropEditor.getEditorDriver().hasErrors();
        }
        if (argGrpPropertyEditor != null) {
            argGrpPropertyEditorHasErrors = argGrpPropertyEditor.getEditorDriver().hasErrors();
        }
        return editorDriver.hasErrors() || currArgPropEditorHasErrors || argGrpPropertyEditorHasErrors;
    }

    @Override
    public void onAppTemplateSelected(AppTemplateSelectedEvent appTemplateSelectedEvent) {
        setEastWidget(defaultDetailsPanel);
    }

    @Override
    public void onArgumentGroupSelected(ArgumentGroupSelectedEvent event) {
        ArgumentGroup selectedArgumentGroup = event.getArgumentGroup();
        if (selectedArgumentGroup == null) {
            setEastWidget(defaultDetailsPanel);
        }

        if (argGrpPropertyEditor == null) {
            argGrpPropertyEditor = argGrpPropEditorProvider.get();
            argGrpPropertyEditor.addDeleteArgumentGroupEventHandler(presenter);
        }

        argGrpPropertyEditor.edit(selectedArgumentGroup, event.getAbsoluteEditorPath());
        setEastWidget(argGrpPropertyEditor);

        if ((argGrpPropertyEditor != null) && (event.getSource() instanceof ArgumentGroupEditor)) {
            ArgumentGroupEditor age = (ArgumentGroupEditor)event.getSource();
            argGrpPropertyEditor.setBoundArgumentGroupEditor(age);
        }
    }

    @Override
    public void onArgumentSelected(ArgumentSelectedEvent event) {
        currArgumentPropEditor = null;
        Argument selectedArgument = event.getArgument();
        if (selectedArgument == null) {
            setEastWidget(defaultDetailsPanel);
            return;
        }

        ArgumentType type = selectedArgument.getType();
        if (!propertyEditorMap.containsKey(type)) {
            switch (type) {
                case FileInput:
                    propertyEditorMap.put(type, fileInputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case FolderInput:
                    propertyEditorMap.put(type, folderInputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case MultiFileSelector:
                    propertyEditorMap.put(type, multiFileInputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case Text:
                    propertyEditorMap.put(type, textInputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case EnvironmentVariable:
                    propertyEditorMap.put(type, envVarProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case MultiLineText:
                    propertyEditorMap.put(type, multiLineInputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case Double:
                    propertyEditorMap.put(type, decimalInputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case Integer:
                    propertyEditorMap.put(type, integerInputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case Flag:
                    propertyEditorMap.put(type, flagProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case TextSelection:
                    propertyEditorMap.put(type, textSelectionProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case IntegerSelection:
                    propertyEditorMap.put(type, integerSelectionProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;
                case DoubleSelection:
                    propertyEditorMap.put(type, decimalSelectionProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;
                case TreeSelection:
                    propertyEditorMap.put(type, treeSelectionProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case Info:
                    propertyEditorMap.put(type, infoProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case FileOutput:
                    propertyEditorMap.put(type, fileOutputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;
                case FolderOutput:
                    propertyEditorMap.put(type, folderOutputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;
                case MultiFileOutput:
                    propertyEditorMap.put(type, multiFileOutputProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                case ReferenceAnnotation:
                    propertyEditorMap.put(type, refAnnotationProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;
                case ReferenceGenome:
                    propertyEditorMap.put(type, refGenomeProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;
                case ReferenceSequence:
                    propertyEditorMap.put(type, refSequenceProvider.get());
                    currArgumentPropEditor = propertyEditorMap.get(type);
                    currArgumentPropEditor.edit(selectedArgument);
                    setEastWidget(currArgumentPropEditor);
                    break;

                default:
                    break;
            }

            currArgumentPropEditor.addUpdateCommandLinePreviewEventHandler(presenter);
        } else {
            currArgumentPropEditor = propertyEditorMap.get(type);
            currArgumentPropEditor.edit(selectedArgument);
            setEastWidget(currArgumentPropEditor);
        }

        /*
         * Pass the selected ArgumentEditor to the current property editor. This is to coordinate changes
         * to/from the two editors.
         */
        if ((currArgumentPropEditor != null) && (event.getSource() instanceof ArgumentEditor)) {
            ArgumentEditor currArgEditor = (ArgumentEditor)event.getSource();
            currArgumentPropEditor.setBoundArgumentEditor(currArgEditor);

            currArgumentPropEditor.setLabelOnlyEditMode(presenter.isLabelOnlyEditMode());
        }

    }

    @Override
    public void setCmdLinePreview(String preview) {
        cmdLinePreview.setText(preview);
    }

    @Override
    public void setEastWidget(IsWidget widget) {
        if (widget == null) {
            widget = defaultDetailsPanel;
        }

        eastPanel.setActiveWidget(widget);
    }

    @Override
    public void setOnlyLabelEditMode(boolean onlyLabelEditMode) {
        palette.setOnlyLabelEditMode(onlyLabelEditMode);
    }

    @Override
    public void setPresenter(AppsEditorView.Presenter presenter) {
        this.presenter = presenter;
        toolbar.setPresenter(presenter);
    }

    @UiFactory
    ToolButton createToolBtn() {
        final ToolButton toolButton = new ToolButton(style.contextualHelp());
        toolButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                ContextualHelpPopup popup = new ContextualHelpPopup();
                popup.setWidth(450);
                popup.add(new HTML(helpMessages.appCategorySection()));
                popup.showAt(toolButton.getAbsoluteLeft(), toolButton.getAbsoluteTop() + 15);
            }
        });
        return toolButton;
    }

}
