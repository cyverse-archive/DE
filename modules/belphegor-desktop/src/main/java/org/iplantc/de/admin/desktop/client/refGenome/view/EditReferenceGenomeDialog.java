package org.iplantc.de.admin.desktop.client.refGenome.view;

import org.iplantc.de.admin.desktop.client.I18N;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenomeAutoBeanFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Date;

class EditReferenceGenomeDialog extends Composite implements Editor<ReferenceGenome>, TakesValue<ReferenceGenome> {

    private static EditReferenceGenomeDialogUiBinder uiBinder = GWT.create(EditReferenceGenomeDialogUiBinder.class);

    interface EditReferenceGenomeDialogUiBinder extends UiBinder<Widget, EditReferenceGenomeDialog> {}

    interface EditorDriver extends SimpleBeanEditorDriver<ReferenceGenome, EditReferenceGenomeDialog> {}

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @UiField
    FieldLabel nameLabel, pathLabel;
    @UiField
    TextField nameEditor, pathEditor, createdByEditor, lastModifiedByEditor;

    @UiField
    DateField createdDateEditor, lastModifiedDateEditor;

    @UiField
    CheckBox deletedEditor;

    static EditReferenceGenomeDialog addNewReferenceGenome() {
        ReferenceGenomeAutoBeanFactory factory = GWT.create(ReferenceGenomeAutoBeanFactory.class);
        ReferenceGenome refGenome = factory.referenceGenome().as();
        refGenome.setCreatedBy(UserInfo.getInstance().getUsername());
        refGenome.setLastModifiedBy(UserInfo.getInstance().getUsername());
        Date currDate = new Date();
        refGenome.setCreatedDate(currDate);
        refGenome.setLastModifiedDate(currDate);

        EditReferenceGenomeDialog addRegGenPanel = new EditReferenceGenomeDialog(refGenome);
        addRegGenPanel.deletedEditor.setEnabled(false);
        return addRegGenPanel;
    }

    static EditReferenceGenomeDialog editReferenceGenome(ReferenceGenome refGenome) {
        EditReferenceGenomeDialog editRefGenomePanel = new EditReferenceGenomeDialog(refGenome);
        editRefGenomePanel.setTitle(refGenome.getName());
        editRefGenomePanel.deletedEditor.setEnabled(true);
        return editRefGenomePanel;
    }

    interface Templates extends XTemplates {
        @XTemplate("<span style='color: red;'>*&nbsp</span>{label}")
        SafeHtml requiredFieldLabel(String label);
    }

    private final Templates templates = GWT.create(Templates.class);
    private EditReferenceGenomeDialog(ReferenceGenome refGenome) {
        initWidget(uiBinder.createAndBindUi(this));

        nameLabel.setHTML(templates.requiredFieldLabel(I18N.DISPLAY.name()));
        pathLabel.setHTML(templates.requiredFieldLabel(I18N.DISPLAY.path()));

        editorDriver.initialize(this);
        editorDriver.edit(refGenome);
    }

    @Override
    public void setValue(ReferenceGenome value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReferenceGenome getValue() {
        return editorDriver.flush();
    }

    boolean hasErrors() {
        return editorDriver.hasErrors();
    }
}
