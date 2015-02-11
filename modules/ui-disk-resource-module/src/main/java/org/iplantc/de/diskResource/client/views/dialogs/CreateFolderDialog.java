package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author jstroot
 */
public class CreateFolderDialog extends IPlantPromptDialog {

    public interface Appearance {

        String dialogWidth();

        String folderName();

        String newFolder();

        SafeHtml renderDestinationPathLabel(String destPath, String createIn);
    }

    private final Appearance appearance;

    private final DiskResourceUtil diskResourceUtil;
    public CreateFolderDialog(final Folder parentFolder) {
        this(parentFolder,
             GWT.<Appearance> create(Appearance.class));
    }

    public CreateFolderDialog(final Folder parentFolder,
                              final Appearance appearance) {
        super(appearance.folderName(), -1, "", new DiskResourceNameValidator());
        this.appearance = appearance;
        diskResourceUtil = DiskResourceUtil.getInstance();
        setWidth(appearance.dialogWidth());
        setHeadingText(appearance.newFolder());
        initDestPathLabel(parentFolder.getPath());
    }

    private void initDestPathLabel(String destPath) {

        HTML htmlDestText = new HTML(appearance.renderDestinationPathLabel(destPath, diskResourceUtil.parseNameFromPath(destPath)));
        insert(htmlDestText, 0);
    }
}
