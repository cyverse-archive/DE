package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.SimpleFileUploadDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.util.Format;

import java.util.List;

/**
 * @author jstroot
 */
public class SimpleFileUploadDialogDefaultAppearance implements SimpleFileUploadDialog.SimpleFileUploadDialogAppearance {

    public interface Templates extends SafeHtmlTemplates {
        @Template("<div title='{0}' style='color: #0098AA;width:100%;padding:5px;text-overflow:ellipsis;'>{1}</div>")
        SafeHtml destinationPathLabel(String destPath, String parentPath);
    }

    public interface SimpleFileUploadAppearanceResources extends ClientBundle {

        @Source("../arrow_undo.png")
        ImageResource arrowUndoIcon();
    }

    private final DiskResourceMessages diskResourceMessages;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantErrorStrings iplantErrorStrings;
    private final Templates templates;
    private final SimpleFileUploadAppearanceResources resources;

    public SimpleFileUploadDialogDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class),
             GWT.<Templates> create(Templates.class),
             GWT.<SimpleFileUploadAppearanceResources> create(SimpleFileUploadAppearanceResources.class));
    }

    SimpleFileUploadDialogDefaultAppearance(final DiskResourceMessages diskResourceMessages,
                                            final IplantDisplayStrings iplantDisplayStrings,
                                            final IplantErrorStrings iplantErrorStrings,
                                            final Templates templates,
                                            final SimpleFileUploadAppearanceResources resources) {
        this.diskResourceMessages = diskResourceMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
        this.templates = templates;
        this.resources = resources;
    }

    @Override
    public String confirmAction() {
        return iplantDisplayStrings.confirmAction();
    }

    @Override
    public String fileExist() {
        return iplantErrorStrings.fileExist();
    }

    @Override
    public String fileUploadMaxSizeWarning() {
        return diskResourceMessages.fileUploadMaxSizeWarning();
    }

    @Override
    public ImageResource arrowUndoIcon() {
        return resources.arrowUndoIcon();
    }

    @Override
    public String fileUploadsFailed(List<String> strings) {
        return iplantErrorStrings.fileUploadsFailed(strings);
    }

    @Override
    public String closeConfirmMessage() {
        return iplantDisplayStrings.transferCloseConfirmMessage();
    }

    @Override
    public SafeHtml renderDestinationPathLabel(String destPath, String parentPath) {
        final String truncatedParent = Format.ellipse(diskResourceMessages.uploadingToFolder(parentPath), 50);
        return templates.destinationPathLabel(destPath, truncatedParent);
    }

    @Override
    public String reset() {
        return diskResourceMessages.reset();
    }

    @Override
    public String upload() {
        return iplantDisplayStrings.upload();
    }

}
