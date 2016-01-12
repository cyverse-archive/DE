package org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.DeleteToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.SaveToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.ToolAdminDetailsView;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolContainer;
import org.iplantc.de.client.models.tool.ToolDevice;
import org.iplantc.de.client.models.tool.ToolImplementation;
import org.iplantc.de.client.models.tool.ToolTestData;
import org.iplantc.de.client.models.tool.ToolVolume;
import org.iplantc.de.client.models.tool.ToolVolumesFrom;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * @author jstroot
 * @author aramsey
 */
public class ToolAdminDetailsDialog extends IPlantDialog implements IsHideable,
                                                                    SaveToolSelectedEvent.HasSaveToolSelectedEventHandlers,
                                                                    DeleteToolSelectedEvent.HasDeleteToolSelectedEventHandlers {

    private final ToolAdminDetailsView view;
    private final ToolAdminView.ToolAdminViewAppearance appearance;
    @Inject
    ToolAutoBeanFactory factory;

    @Inject
    public ToolAdminDetailsDialog(final ToolAdminDetailsView view,
                                  final ToolAdminView.ToolAdminViewAppearance appearance) {
        super(true);
        this.view = view;
        this.appearance = appearance;

        setHideOnButtonClick(false);
        setHeadingText(appearance.dialogWindowName());
        setResizable(true);
        setPixelSize(1000, 500);
        setMinHeight(200);
        setMinWidth(500);

        addHelp(new HTML(appearance.toolAdminHelp()));

        setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL, PredefinedButton.CLOSE);
        getOkButton().setText(appearance.dialogWindowUpdateBtnText());
        getDeleteButton().setText(appearance.dialogWindowDeleteBtnText());

        addOkButtonSelectHandler(new OkSelectHandler(this, this, this.appearance, this.view));
        addCancelButtonSelectHandler(new CancelSelectHandler());
        getDeleteButton().addSelectHandler(new DeleteSelectHandler(this, view));

        FlowLayoutContainer container = new FlowLayoutContainer();
        container.getScrollSupport().setScrollMode(ScrollSupport.ScrollMode.AUTO);
        container.add(this.view);
        add(container);
    }

    TextButton getDeleteButton() {
        return getButton(PredefinedButton.CLOSE);
    }

    public void show(final Tool tool) {
        view.edit(tool);
        super.show();
    }

    @Override
    public void show() {
        final TextButton deleteButton = getDeleteButton();
        deleteButton.disable();
        deleteButton.setVisible(false);

        final ToolContainer toolContainer = factory.getContainer().as();
        toolContainer.setDeviceList(Lists.<ToolDevice>newArrayList());
        toolContainer.setContainerVolumes(Lists.<ToolVolume>newArrayList());
        toolContainer.setContainerVolumesFrom(Lists.<ToolVolumesFrom>newArrayList());
        toolContainer.setImage(factory.getImage().as());

        final ToolImplementation toolImplementation = factory.getImplementation().as();
        final ToolTestData toolTestData = factory.getTest().as();
        toolTestData.setInputFiles(Lists.<String>newArrayList());
        toolTestData.setOutputFiles(Lists.<String>newArrayList());
        toolImplementation.setTest(toolTestData);

        final Tool tool = factory.getTool().as();
        tool.setType(appearance.toolImportTypeDefaultValue());
        tool.setContainer(toolContainer);
        tool.setImplementation(toolImplementation);

        show(tool);
    }

    @Override
    public HandlerRegistration addDeleteToolSelectedEventHandler(DeleteToolSelectedEvent.DeleteToolSelectedEventHandler handler) {
        return addHandler(handler, DeleteToolSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addSaveToolSelectedEventHandler(SaveToolSelectedEvent.SaveToolSelectedEventHandler handler) {
        return addHandler(handler, SaveToolSelectedEvent.TYPE);
    }


    private static class OkSelectHandler implements SelectEvent.SelectHandler {
        private final IsHideable hideable;
        private final HasHandlers hasHandlers;
        private ToolAdminView.ToolAdminViewAppearance appearance;
        private ToolAdminDetailsView view;


        private OkSelectHandler(IsHideable hideable,
                                HasHandlers hasHandlers,
                                ToolAdminView.ToolAdminViewAppearance appearance,
                                ToolAdminDetailsView view) {
            this.hideable = hideable;
            this.hasHandlers = hasHandlers;
            this.appearance = appearance;
            this.view = view;
        }

        @Override
        public void onSelect(SelectEvent event) {
            if (view.isValid()) {
                SaveToolSelectedEvent saveToolSelectedEvent = new SaveToolSelectedEvent(view.getTool());
                hasHandlers.fireEvent(saveToolSelectedEvent);
                hideable.hide();
            } else {
                AlertMessageBox alertMsgBox =
                        new AlertMessageBox("Warning", appearance.completeRequiredFieldsError());
                alertMsgBox.show();
            }
        }
    }

    private class CancelSelectHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            hide();
        }
    }

    private class DeleteSelectHandler implements SelectEvent.SelectHandler {
        private final HasHandlers hasHandlers;
        private final ToolAdminDetailsView view;

        public DeleteSelectHandler(HasHandlers hasHandlers, ToolAdminDetailsView view) {
            this.hasHandlers = hasHandlers;
            this.view = view;
        }

        @Override
        public void onSelect(SelectEvent event) {
            final Tool tool = view.getTool();
            final ConfirmMessageBox deleteMsgBox =
                    new ConfirmMessageBox("Confirm", "Delete " + tool.getName() + "?");
            deleteMsgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (event.getHideButton().equals(PredefinedButton.OK) || event.getHideButton()
                                                                                  .equals(PredefinedButton.YES)) {
                        DeleteToolSelectedEvent deleteToolSelectedEvent =
                                new DeleteToolSelectedEvent(tool);
                        hasHandlers.fireEvent(deleteToolSelectedEvent);
                        deleteMsgBox.hide();
                        hide();
                    }
                }
            });
            deleteMsgBox.show();
        }
    }
}
