package org.iplantc.de.admin.desktop.client.toolRequest.view;

import org.iplantc.de.admin.desktop.client.toolRequest.ToolRequestView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestAutoBeanFactory;
import org.iplantc.de.client.models.toolRequest.ToolRequestStatus;
import org.iplantc.de.client.models.toolRequest.ToolRequestUpdate;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class UpdateToolRequestDialog extends IPlantDialog {

    private static UpdateToolRequestPanelUiBinder uiBinder = GWT.create(UpdateToolRequestPanelUiBinder.class);

    @UiTemplate("UpdateToolRequestPanel.ui.xml")
    interface UpdateToolRequestPanelUiBinder extends UiBinder<Widget, UpdateToolRequestDialog> {
    }
    
    @UiField Label currentStatusLabel;
    @UiField SimpleComboBox<ToolRequestStatus> statusCombo;
    @UiField TextArea commentsEditor;
    @UiField TextField statusField;
    @UiField(provided = true) ToolRequestView.ToolRequestViewAppearance appearance = GWT.create(ToolRequestView.ToolRequestViewAppearance.class);

    private final ToolRequestAutoBeanFactory factory;

    public UpdateToolRequestDialog(final ToolRequest toolRequest,
                                   final ToolRequestAutoBeanFactory factory) {
        this.factory = factory;
        setHeadingText(appearance.updateToolRequestDlgHeading());
        getOkButton().setText(appearance.submitBtnText());
        add(uiBinder.createAndBindUi(this));
        currentStatusLabel.setText(toolRequest.getStatus());
        statusField.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(KeyPressEvent event) {
                statusCombo.reset();
            }
        });
        commentsEditor.setHeight(200);
    }

    @UiFactory
    SimpleComboBox<ToolRequestStatus> createComboBox() {
        SimpleComboBox<ToolRequestStatus> cb = new SimpleComboBox<>(new LabelProvider<ToolRequestStatus>() {

            @Override
            public String getLabel(ToolRequestStatus item) {
                return item.name();
            }
        });

        cb.add(ToolRequestStatus.Submitted);
        cb.add(ToolRequestStatus.Pending);
        cb.add(ToolRequestStatus.Evaluation);
        cb.add(ToolRequestStatus.Installation);
        cb.add(ToolRequestStatus.Validation);
        cb.add(ToolRequestStatus.Completion);
        cb.add(ToolRequestStatus.Failed);

        cb.addSelectionHandler(new SelectionHandler<ToolRequestStatus>() {

            @Override
            public void onSelection(SelectionEvent<ToolRequestStatus> event) {
                if (statusField != null) {
                    statusField.clear();
                }

            }
        });
        return cb;
    }

    public ToolRequestUpdate getToolRequestUpdate() {
        ToolRequestUpdate trUpdate = factory.update().as();
        trUpdate.setStatus(statusCombo.getCurrentValue() != null ? (statusCombo.getCurrentValue().toString()) : statusField.getCurrentValue());
        trUpdate.setComments(commentsEditor.getCurrentValue());
        return trUpdate;
    }

    @Override
    public void show() {
        super.show();
        ensureDebugId(Belphegor.ToolRequestIds.TOOL_REQUEST_DIALOG);
        getWidget().ensureDebugId(Belphegor.ToolRequestIds.TOOL_REQUEST_DIALOG + Belphegor.ToolRequestIds.VIEW);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        getOkButton().ensureDebugId(baseID + Belphegor.ToolRequestIds.SUBMIT_BTN);
        currentStatusLabel.ensureDebugId(baseID + Belphegor.ToolRequestIds.DIALOG_VIEW + Belphegor.ToolRequestIds.CURRENT_STATUS);
        statusCombo.setId(baseID + Belphegor.ToolRequestIds.DIALOG_VIEW + Belphegor.ToolRequestIds.STATUS_COMBO);
        commentsEditor.setId(baseID + Belphegor.ToolRequestIds.DIALOG_VIEW + Belphegor.ToolRequestIds.COMMENTS);
        statusField.setId(baseID + Belphegor.ToolRequestIds.DIALOG_VIEW + Belphegor.ToolRequestIds.STATUS);
    }
}
