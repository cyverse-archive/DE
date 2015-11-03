package org.iplantc.de.theme.base.client.diskResource.dataLink;

import org.iplantc.de.diskResource.client.DataLinkView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class DataLinkViewDefaultAppearance implements DataLinkView.Appearance {
    public static interface DataLinkPanelCellStyle extends CssResource {
        String background();

        String dataLinkInfoImg();

        String dataLinkWarning();

        String pasteIcon();
    }

    public interface DataLinkResources extends ClientBundle {
        @Source("DataLinkPanelCell.css")
        DataLinkPanelCellStyle css();

        @Source("../link_add.png")
        ImageResource linkAdd();

        @Source("../paste_plain.png")
        ImageResource paste();

        @Source("../tree_collapse.png")
        ImageResource treeCollapse();

        @Source("../tree_expand.png")
        ImageResource treeExpand();
    }

    private final DataLinkPanelCellStyle css;
    private final DiskResourceMessages diskResourceMessages;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantResources iplantResources;
    private final DataLinkResources resources;
    private final DataLinkMessages displayMessages;

    public DataLinkViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings>create(IplantDisplayStrings.class),
             GWT.<DiskResourceMessages>create(DiskResourceMessages.class),
             GWT.<IplantResources>create(IplantResources.class),
             GWT.<DataLinkResources>create(DataLinkResources.class),
             GWT.<DataLinkMessages> create(DataLinkMessages.class));
    }

    DataLinkViewDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                  final DiskResourceMessages diskResourceMessages,
                                  final IplantResources iplantResources,
                                  final DataLinkResources resources,
                                  final DataLinkMessages displayMessages) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.diskResourceMessages = diskResourceMessages;
        this.iplantResources = iplantResources;
        this.resources = resources;
        this.displayMessages = displayMessages;
        css = resources.css();
        css.ensureInjected();
    }

    @Override
    public String advancedSharing() {
        return displayMessages.advancedSharing();
    }

    @Override
    public String backgroundClass() {
        return css.background();
    }

    @Override
    public String collapseAll() {
        return diskResourceMessages.collapseAll();
    }

    @Override
    public String copy() {
        return iplantDisplayStrings.copy();
    }

    @Override
    public String copyDataLinkDlgHeight() {
        return "130";
    }

    @Override
    public int copyDataLinkDlgTextBoxWidth() {
        return 500;
    }

    @Override
    public String copyDataLinkDlgWidth() {
        return "535";
    }

    @Override
    public String copyLink() {
        return displayMessages.copyLink();
    }

    @Override
    public String copyPasteInstructions() {
        return iplantDisplayStrings.copyPasteInstructions();
    }

    @Override
    public String create() {
        return iplantDisplayStrings.create();
    }

    @Override
    public String dataLinkInfoImgClass() {
        return css.dataLinkInfoImg();
    }

    @Override
    public String dataLinkWarning() {
        return displayMessages.dataLinkWarning();
    }

    @Override
    public String dataLinkWarningClass() {
        return css.dataLinkWarning();
    }

    @Override
    public ImageResource exclamationIcon() {
        return iplantResources.exclamation();
    }

    @Override
    public String expandAll() {
        return displayMessages.expandAll();
    }

    @Override
    public ImageResource infoIcon() {
        return iplantResources.info();
    }

    @Override
    public ImageResource linkAddIcon() {
        return resources.linkAdd();
    }

    @Override
    public String loadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public ImageResource pasteIcon() {
        return resources.paste();
    }

    @Override
    public ImageResource treeCollapseIcon() {
        return resources.treeCollapse();
    }

    @Override
    public ImageResource treeExpandIcon() {
        return resources.treeExpand();
    }
}
