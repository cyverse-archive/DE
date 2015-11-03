package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.CreateNcbiSraFolderStructureDialog;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.util.Format;


public class CreateNcbiSraFolderStructureDefaultAppearance implements
                                                         CreateNcbiSraFolderStructureDialog.Appearance {

    public interface Templates extends SafeHtmlTemplates {
        @Template("<div title='{0}' style='color: #0098AA;width:100%;padding:5px;text-overflow:ellipsis;'>{1}</div>")
                SafeHtml
                destinationPathLabel(String destPath, String createIn);
    }

    private final DiskResourceMessages diskResourceMessages;
    private final Templates templates;

    public CreateNcbiSraFolderStructureDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<Templates> create(Templates.class));
    }

    public CreateNcbiSraFolderStructureDefaultAppearance(final DiskResourceMessages diskResourceMessages,
                                                        final Templates templates) {
        this.diskResourceMessages = diskResourceMessages;
        this.templates = templates;
    }

    @Override
    public String dialogWidth() {
        return "300px";
    }

    @Override
    public String projectName() {
        return diskResourceMessages.projectName();
    }

    @Override
    public String numberOfBioSamples() {
        return diskResourceMessages.numberOfBioSamples();
    }

    @Override
    public String numberOfLib() {
        return diskResourceMessages.numberOfLib();
    }

    @Override
    public String ncbiSraProject() {
        return diskResourceMessages.ncbiSraProject();
    }

    @Override
    public SafeHtml renderDestinationPathLabel(String destPath, String createIn) {
        return templates.destinationPathLabel(destPath,
                                              Format.ellipse(diskResourceMessages.createIn(createIn), 50));
    }

}
