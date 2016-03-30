package org.iplantc.de.collaborators.client.views;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.collaborators.client.models.CollaboratorNameComparator;
import org.iplantc.de.collaborators.client.models.CollaboratorProperties;
import org.iplantc.de.collaborators.client.views.cells.NameCell;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jstroot
 */
public class CollaboratorsColumnModel extends ColumnModel<Collaborator> {

    private final ManageCollaboratorsView.Appearance appearance;

    public CollaboratorsColumnModel(final CheckBoxSelectionModel<Collaborator> checkBoxModel) {
        this(checkBoxModel,
             GWT.<CollaboratorProperties> create(CollaboratorProperties.class),
             GWT.<ManageCollaboratorsView.Appearance> create(ManageCollaboratorsView.Appearance.class));
    }

    public CollaboratorsColumnModel(final CheckBoxSelectionModel<Collaborator> checkBoxModel,
                                    final CollaboratorProperties properties,
                                    final ManageCollaboratorsView.Appearance appearance) {
        super(createColumnConfigList(checkBoxModel,
                                     properties,
                                     appearance));
        this.appearance = appearance;
    }

    static List<ColumnConfig<Collaborator, ?>> createColumnConfigList(final CheckBoxSelectionModel<Collaborator> checkBoxModel,
                                                                      final CollaboratorProperties properties,
                                                                      final ManageCollaboratorsView.Appearance appearance) {

        List<ColumnConfig<Collaborator, ?>> configs = new ArrayList<>();

        ColumnConfig<Collaborator, Collaborator> colCheckBox = checkBoxModel.getColumn();
        configs.add(colCheckBox);

        ColumnConfig<Collaborator, Collaborator> name = new ColumnConfig<>(new IdentityValueProvider<Collaborator>("firstname"),
                                                                                                     150);
        name.setHeader(I18N.DISPLAY.name());
        name.setCell(new NameCell());

        name.setComparator(new CollaboratorNameComparator());
        configs.add(name);

        ColumnConfig<Collaborator, String> ins = new ColumnConfig<>(properties.institution(),
                                                                                        150);
        ins.setHeader(I18N.DISPLAY.institution());
        configs.add(ins);

        return configs;

    }
}
