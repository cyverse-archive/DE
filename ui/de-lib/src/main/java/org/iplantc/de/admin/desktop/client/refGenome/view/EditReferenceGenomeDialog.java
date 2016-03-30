package org.iplantc.de.admin.desktop.client.refGenome.view;

import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenomeAutoBeanFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Date;

/**
 * @author jstroot
 */
class EditReferenceGenomeDialog extends Composite implements Editor<ReferenceGenome>,
                                                             TakesValue<ReferenceGenome> {

    private static EditReferenceGenomeDialogUiBinder uiBinder = GWT.create(EditReferenceGenomeDialogUiBinder.class);

    interface EditReferenceGenomeDialogUiBinder extends UiBinder<Widget, EditReferenceGenomeDialog> {}

    interface EditorDriver extends SimpleBeanEditorDriver<ReferenceGenome, EditReferenceGenomeDialog> {}

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @UiField FieldLabel nameLabel, pathLabel;
    @UiField TextField nameEditor, pathEditor, createdByEditor, lastModifiedByEditor;
    @UiField DateField createdDateEditor, lastModifiedDateEditor;
    @UiField CheckBoxAdapter deletedEditor;
    @UiField(provided = true) RefGenomeView.RefGenomeAppearance appearance = GWT.create(RefGenomeView.RefGenomeAppearance.class);

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

    private EditReferenceGenomeDialog(ReferenceGenome refGenome) {
        initWidget(uiBinder.createAndBindUi(this));

        nameLabel.setHTML(appearance.requiredNameLabel());
        pathLabel.setHTML(appearance.requiredPathLabel());

        editorDriver.initialize(this);
        editorDriver.edit(refGenome);
        ensureDebugId(Belphegor.RefGenomeIds.GENOME_EDITOR + Belphegor.RefGenomeIds.EDITOR_VIEW);
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

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        nameLabel.ensureDebugId(baseID + Belphegor.RefGenomeIds.NAME_LABEL);
        pathLabel.ensureDebugId(baseID + Belphegor.RefGenomeIds.PATH_LABEL);
        nameEditor.setId(baseID + Belphegor.RefGenomeIds.NAME);
        pathEditor.setId(baseID + Belphegor.RefGenomeIds.PATH);
        createdByEditor.setId(baseID + Belphegor.RefGenomeIds.CREATED_BY);
        lastModifiedByEditor.setId(baseID + Belphegor.RefGenomeIds.LAST_MODIFIED_BY);
        createdDateEditor.setId(baseID + Belphegor.RefGenomeIds.CREATED_DATE);
        lastModifiedDateEditor.setId(baseID + Belphegor.RefGenomeIds.LAST_MODIFIED_DATE);
        deletedEditor.getCheckBox().ensureDebugId(baseID + Belphegor.RefGenomeIds.DELETED);
    }
}
