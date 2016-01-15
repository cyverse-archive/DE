package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestViewAppearance;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;

/**
 * 
 * @author sriram
 * 
 */
public class UpdatePermanentIdRequestDialog extends IPlantDialog {

    private static UpdatePermanentIdRequestUiBinder uiBinder = GWT.create(UpdatePermanentIdRequestUiBinder.class);

    @UiTemplate("UpdatePermanentIdRequest.ui.xml")
    interface UpdatePermanentIdRequestUiBinder extends UiBinder<Widget, UpdatePermanentIdRequestDialog> {
    }

    @UiField
    Label currentStatusLabel;
    @UiField
    SimpleComboBox<PermanentIdRequestStatus> statusCombo;
    @UiField
    TextArea commentsEditor;
    private final PermanentIdRequestAutoBeanFactory factory;
    @SuppressWarnings("unused")
    private final PermanentIdRequest request;
    @SuppressWarnings("unused")
    private final PermanentIdRequestView.Presenter presenter;

    
    public UpdatePermanentIdRequestDialog(final PermanentIdRequest request,
                                          final PermanentIdRequestView.Presenter presenter,
                                          final PermanentIdRequestAutoBeanFactory factory) {

        this.factory = factory;
        this.request = request;
        this.presenter = presenter;
        add(uiBinder.createAndBindUi(this));
        currentStatusLabel.setText(request.getStatus());
        commentsEditor.setHeight(200);

    }

    @UiFactory
    SimpleComboBox<PermanentIdRequestStatus> createComboBox() {
        SimpleComboBox<PermanentIdRequestStatus> cb = new SimpleComboBox<>(new LabelProvider<PermanentIdRequestStatus>() {

            @Override
            public String getLabel(PermanentIdRequestStatus item) {
                return item.name();
            }
        });

        cb.add(PermanentIdRequestStatus.Submitted);
        cb.add(PermanentIdRequestStatus.Pending);
        cb.add(PermanentIdRequestStatus.Evaluation);
        cb.add(PermanentIdRequestStatus.Approved);
        cb.add(PermanentIdRequestStatus.Completion);
        cb.add(PermanentIdRequestStatus.Failed);
        cb.setAllowBlank(false);
        cb.setEditable(false);
        cb.setTriggerAction(TriggerAction.ALL);
        return cb;
    }

    public PermanentIdRequestUpdate getPermanentIdRequestUpdate() {
        PermanentIdRequestUpdate update = factory.getStatus().as();
        update.setStatus(statusCombo.getCurrentValue().toString());
        update.setComments(commentsEditor.getCurrentValue());
        return update;
    }

}
