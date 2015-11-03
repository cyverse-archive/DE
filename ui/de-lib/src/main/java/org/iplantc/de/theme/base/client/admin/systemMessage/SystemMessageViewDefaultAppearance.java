package org.iplantc.de.theme.base.client.admin.systemMessage;

import org.iplantc.de.admin.desktop.client.systemMessage.SystemMessageView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class SystemMessageViewDefaultAppearance implements SystemMessageView.SystemMessageViewAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final SystemMessageStrings displayStrings;
    private final IplantResources iplantResources;

    public SystemMessageViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<SystemMessageStrings> create(SystemMessageStrings.class));
    }

    SystemMessageViewDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                       final IplantResources iplantResources,
                                       final SystemMessageStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
        this.displayStrings = displayStrings;
    }

    @Override
    public String activationDateColumnLabel() {
        return displayStrings.activationDateColumnLabel();
    }

    @Override
    public int activationDateColumnWidth() {
        return 200;
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String createSystemMsgDlgHeading() {
        return displayStrings.createSystemMsgDlgHeading();
    }

    @Override
    public String deactivationDateColumnLabel() {
        return displayStrings.deactivationDateColumnLabel();
    }

    @Override
    public int deactivationDateColumnWidth() {
        return 200;
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }

    @Override
    public String dismissibleColumnLabel() {
        return displayStrings.dismissibleColumnLabel();
    }

    @Override
    public int dismissibleColumnWidth() {
        return 90;
    }

    @Override
    public String editSystemMsgDlgHeading() {
        return displayStrings.editSystemMsgDlgHeading();
    }

    @Override
    public int editSystemMsgDlgWidth() {
        return 500;
    }

    @Override
    public String messageColumnLabel() {
        return displayStrings.messageColumnLabel();
    }

    @Override
    public int messageColumnWidth() {
        return 400;
    }

    @Override
    public String submitButtonText() {
        return iplantDisplayStrings.submit();
    }

    @Override
    public String typeColumnLabel() {
        return displayStrings.typeColumnLabel();
    }

    @Override
    public int typeColumnWidth() {
        return 90;
    }

    @Override
    public String systemMsgDlgTypeLabel() {
        return displayStrings.systemMsgDlgTypeLabel();
    }

    @Override
    public String systemMsgDlgMessageLabel() {
        return displayStrings.systemMsgDlgMessageLabel();
    }

    @Override
    public String systemMsgDlgActivationDateLabel() {
        return displayStrings.systemMsgDlgActivationDateLabel();
    }

    @Override
    public String systemMsgDlgActivationTimeLabel() {
        return displayStrings.systemMsgDlgActivationTimeLabel();
    }

    @Override
    public String systemMsgDlgDeactivationDateLabel() {
        return displayStrings.systemMsgDlgDeactivationDateLabel();
    }

    @Override
    public String systemMsgDeactivationTimeLabel() {
        return displayStrings.systemMsgDlgDeactivationTimeLabel();
    }

    @Override
    public String systemMsgDlgDismissibleLabel() {
        return displayStrings.systemMsgDlgDismissibleLabel();
    }

    @Override
    public String systemMsgDlgLoginsDisabledLabel() {
        return displayStrings.systemMsgDlgLoginsDisabledLabel();
    }
}
