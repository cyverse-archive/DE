package org.iplantc.de.apps.integration.client.view.propertyEditors;

import org.iplantc.de.apps.integration.client.view.propertyEditors.widgets.SelectionItemTreePropertyEditor;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.widgets.ContextualHelpPopup;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.TreeSelectionLabels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

public class TreeSelectionPropertyEditor extends AbstractArgumentPropertyEditor {

    interface EditorDriver extends SimpleBeanEditorDriver<Argument, TreeSelectionPropertyEditor> {}
    interface TreeSelectionPropertyEditorUiBinder extends UiBinder<Widget, TreeSelectionPropertyEditor> {}

    private static TreeSelectionPropertyEditorUiBinder uiBinder = GWT.create(TreeSelectionPropertyEditorUiBinder.class);

    @UiField(provided = true)
    AppsWidgetsPropertyPanelLabels appLabels;

    @UiField
    @Path("visible")
    CheckBoxAdapter doNotDisplay;

    @UiField
    TextField label;

    @UiField
    CheckBoxAdapter omitIfBlank, requiredEditor;

    @UiField
    @Path("description")
    TextField toolTipEditor;

    @UiField
    FieldLabel toolTipTextLabel;
    @UiField(provided = true)
    TreeSelectionLabels treeSelectionLabels;

    @Ignore
    @UiField
    TextButton editTreeListBtn;

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public TreeSelectionPropertyEditor(AppTemplateWizardAppearance appearance, AppsWidgetsPropertyPanelLabels appLabels, AppsWidgetsContextualHelpMessages help) {
        super(appearance);
        this.appLabels = appLabels;
        this.treeSelectionLabels = appLabels;
        initWidget(uiBinder.createAndBindUi(this));

        toolTipTextLabel.setHTML(appearance.createContextualHelpLabel(appLabels.toolTipText(), help.toolTip()));
        doNotDisplay.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.doNotDisplay()).toSafeHtml());

        requiredEditor.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.isRequired()).toSafeHtml());
        omitIfBlank.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;")
                .append(appearance.createContextualHelpLabelNoFloat(appLabels.excludeWhenEmpty(), help.singleSelectExcludeArgument())).toSafeHtml());

        editorDriver.initialize(this);
        editorDriver.accept(new InitializeTwoWayBinding(this));
    }

    @UiHandler("editTreeListBtn")
    void onEditTreeListSelected(@SuppressWarnings("unused") SelectEvent event) {
        IPlantDialog dlg = new IPlantDialog();
        dlg.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        dlg.setHeadingText(appearance.getPropertyPanelLabels().singleSelectionCreateLabel());
        dlg.setModal(true);
        dlg.setOkButtonText(I18N.DISPLAY.done());
        dlg.setAutoHide(false);
        final SelectionItemTreePropertyEditor selectionItemTreeEditor = new SelectionItemTreePropertyEditor(model.getSelectionItems());
        dlg.setSize("640", "480");
        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        vlc.setScrollMode(ScrollMode.AUTOY);
        vlc.add(selectionItemTreeEditor, new VerticalLayoutData(1.0, 1.0));
        dlg.add(vlc);
        dlg.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                // Manually grab values
                model.getSelectionItems().clear();

                /*
                 * JDS Grab the AutoBean tag for items which should be removed. This is to communicate to
                 * the center tree store that some items should be removed from the store.
                 */
                final AutoBean<SelectionItemGroup> values = selectionItemTreeEditor.getValues();
                AutoBeanUtils.getAutoBean(model).setTag(SelectionItem.TO_BE_REMOVED, values.getTag(SelectionItem.TO_BE_REMOVED));
                values.setTag(SelectionItem.TO_BE_REMOVED, null);
                model.getSelectionItems().add(values.as());
                /*
                 * Fire value change on an arbitrary LeafValueEditor which is bound in the
                 * InitializeTwoWayBinding visitor
                 */
                ValueChangeEvent.fire(toolTipEditor, model.getDescription());
            }
        });

        final ToolButton toolBtn = new ToolButton(IplantResources.RESOURCES.getContxtualHelpStyle().contextualHelp());
        toolBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                ContextualHelpPopup popup = new ContextualHelpPopup();
                popup.setWidth(450);
                popup.add(new HTML(appearance.getContextHelpMessages().treeSelectionCreateTree()));
                popup.showAt(toolBtn.getAbsoluteLeft(), toolBtn.getAbsoluteTop() + 15);
            }
        });
        dlg.addTool(toolBtn);

        dlg.show();
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
    protected void initLabelOnlyEditMode(boolean isLabelOnlyEditMode) {
        doNotDisplay.setEnabled(!isLabelOnlyEditMode);
        omitIfBlank.setEnabled(!isLabelOnlyEditMode);
        requiredEditor.setEnabled(!isLabelOnlyEditMode);
    }


}