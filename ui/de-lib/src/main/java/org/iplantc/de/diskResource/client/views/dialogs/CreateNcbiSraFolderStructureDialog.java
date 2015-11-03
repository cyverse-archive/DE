package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.form.TextField;

public class CreateNcbiSraFolderStructureDialog extends IPlantDialog {

    public interface Appearance {
        String dialogWidth();

        String projectName();

        String numberOfBioSamples();

        String numberOfLib();

        String ncbiSraProject();

        SafeHtml renderDestinationPathLabel(String destPath, String createIn);
    }

    private final Appearance appearance;

    private TextField projectTxtField;

    private IntegerField biosampNumField;

    private IntegerField libNumField;

    private final VerticalLayoutContainer vlc = new VerticalLayoutContainer();

    public CreateNcbiSraFolderStructureDialog(final Folder parentFolder) {
        this(parentFolder, GWT.<Appearance> create(Appearance.class));
    }

    public CreateNcbiSraFolderStructureDialog(final Folder parentFolder, final Appearance appearance) {
        this.appearance = appearance;
        setWidth(appearance.dialogWidth());
        setHeadingText(appearance.ncbiSraProject());
        initFields();
        initDestPathLabel(parentFolder.getPath());
        build(parentFolder.getPath());
        setWidget(vlc);
    }

    private void initFields() {
        projectTxtField = new TextField();
        projectTxtField.setAllowBlank(false);
        projectTxtField.addValidator(new DiskResourceNameValidator());
        
        biosampNumField = new IntegerField();
        biosampNumField.setAllowBlank(false);
        biosampNumField.setAllowNegative(false);

        libNumField = new IntegerField();
        libNumField.setAllowBlank(false);
        libNumField.setAllowNegative(false);
    }

    private void build(String path) {
        vlc.add(initDestPathLabel(path), new VerticalLayoutData(1, -1));
        vlc.add(new FieldLabel(projectTxtField, appearance.projectName()), new VerticalLayoutData(1, -1));
        vlc.add(new FieldLabel(biosampNumField, appearance.numberOfBioSamples()),
                new VerticalLayoutData(1, -1));
        vlc.add(new FieldLabel(libNumField, appearance.numberOfLib()), new VerticalLayoutData(1, -1));
    }

    private HTML initDestPathLabel(String destPath) {
        HTML htmlDestText = new HTML(appearance.renderDestinationPathLabel(destPath,
                                                                           DiskResourceUtil.getInstance()
                                                                                           .parseNameFromPath(destPath)));
        return htmlDestText;
    }

    public boolean validate() {
        return projectTxtField.isValid() && biosampNumField.isValid() && libNumField.isValid()
                && biosampNumField.getValue() > 0 && libNumField.getValue() > 0;
    }

    public String getProjectTxt() {
        return projectTxtField.getValue();
    }


    public Integer getBiosampNum() {
        return biosampNumField.getValue();
    }


    public Integer getLibNum() {
        return libNumField.getValue();
    }


}
