package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.CreateFolderDialog;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.util.Format;

/**
 * @author jstroot
 */
public class CreateFolderDialogDefaultAppearance implements CreateFolderDialog.Appearance {

    public interface Templates extends SafeHtmlTemplates {
        @Template("<div title='{0}' style='color: #0098AA;width:100%;padding:5px;text-overflow:ellipsis;'>{1}</div>")
        SafeHtml destinationPathLabel(String destPath, String createIn);
    }

    private final DiskResourceMessages diskResourceMessages;
    private final Templates templates;

    public CreateFolderDialogDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<Templates> create(Templates.class));
    }

    CreateFolderDialogDefaultAppearance(final DiskResourceMessages diskResourceMessages,
                                        final Templates templates) {
        this.diskResourceMessages = diskResourceMessages;
        this.templates = templates;
    }


    @Override
    public String dialogWidth() {
        return "300px";
    }

    @Override
    public String folderName() {
        return diskResourceMessages.folderName();
    }

    @Override
    public String newFolder() {
        return diskResourceMessages.newFolder();
    }

    @Override
    public SafeHtml renderDestinationPathLabel(String destPath, String createIn) {
        return templates.destinationPathLabel(destPath,
                                              Format.ellipse(diskResourceMessages.createIn(createIn), 50));
    }
}
