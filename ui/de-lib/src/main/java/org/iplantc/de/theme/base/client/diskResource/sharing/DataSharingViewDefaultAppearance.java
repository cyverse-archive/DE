package org.iplantc.de.theme.base.client.diskResource.sharing;

import org.iplantc.de.client.sharing.SharingAppearance;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.resources.CommonStyles;

/**
 * @author jstroot
 */
public class DataSharingViewDefaultAppearance implements SharingAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantResources iplantResources;
    private final SharingMessages sharingMessages;
    private final DiskResourceMessages diskResourceMessages;
    private final DataSharingResources resources;

    public interface DataSharingResources extends ClientBundle {

        @Source("../group_key.png")
        ImageResource shareIcon();
    }

    public DataSharingViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<SharingMessages> create(SharingMessages.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<DataSharingResources> create(DataSharingResources.class));
    }

    DataSharingViewDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                     final IplantResources iplantResources,
                                     final SharingMessages sharingMessages,
                                     final DiskResourceMessages diskResourceMessages,
                                     final DataSharingResources resources) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
        this.sharingMessages = sharingMessages;
        this.diskResourceMessages = diskResourceMessages;
        this.resources = resources;
    }

    @Override
    public String chooseFromCollab() {
        return sharingMessages.chooseFromCollab();
    }

    @Override
    public int dataSharingDlgNameColumnWidth() {
        return 130;
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }

    @Override
    public String done() {
        return iplantDisplayStrings.done();
    }

    @Override
    public String explain() {
        return sharingMessages.explain();
    }

    @Override
    public ImageResource folderIcon() {
        return iplantResources.folder();
    }

    @Override
    public String groupByData() {
        return sharingMessages.groupByData();
    }

    @Override
    public String groupByUser() {
        return sharingMessages.groupByUser();
    }

    @Override
    public ImageResource helpIcon() {
        return iplantResources.help();
    }

    @Override
    public String loadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String manageSharing() {
        return sharingMessages.manageSharing();
    }

    @Override
    public String nameColumnLabel() {
        return iplantDisplayStrings.name();
    }

    @Override
    public int nameColumnWidth() {
        return 200;
    }

    @Override
    public String permissionsColumnLabel() {
        return diskResourceMessages.permissions();
    }

    @Override
    public SafeStyles permissionsColumnStyle() {
        return SafeStylesUtils.fromTrustedString("padding: 2px 3px;color:#0098AA;cursor:pointer;");
    }

    @Override
    public int permissionsColumnWidth() {
        return 170;
    }

    @Override
    public SafeStyles removeColumnStyle() {
        return SafeStylesUtils.fromTrustedString("padding: 1px 3px;cursor:pointer;");
    }

    @Override
    public String removeColumnTextClass() {
        return CommonStyles.get().inlineBlock();
    }

    @Override
    public int removeColumnWidth() {
        return 50;
    }

    @Override
    public String selfShareWarning() {
        return sharingMessages.selfShareWarning();
    }

    @Override
    public int shareBreakDownDlgHeight() {
        return 375;
    }

    @Override
    public int shareBreakDownDlgNameColumnWidth() {
        return 120;
    }

    @Override
    public int shareBreakDownDlgPermissionColumnWidth() {
        return 80;
    }

    @Override
    public int shareBreakDownDlgToolbarHeight() {
        return 30;
    }

    @Override
    public int shareBreakDownDlgWidth() {
        return 400;
    }

    @Override
    public ImageResource shareIcon() {
        return resources.shareIcon();
    }

    @Override
    public SafeHtml sharePermissionsHelp() {
        return sharingMessages.sharePermissionsHelp();
    }

    @Override
    public SafeHtml unshare() {
        return sharingMessages.unshare();
    }

    @Override
    public String variablePermissionsNotice() {
        return sharingMessages.variablePermissionsNotice();
    }

    @Override
    public String warning() {
        return iplantDisplayStrings.warning();
    }

    @Override
    public String whoHasAccess() {
        return sharingMessages.whoHasAccess();
    }

    @Override
    public String selectFilesFolders() {
        return sharingMessages.selectFilesFolders();
    }
}
