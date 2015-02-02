package org.iplantc.de.theme.base.client.diskResource.toolbar.presenter;

import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.toolbar.ToolbarDisplayMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class ToolbarViewPresenterDefaultAppearance implements ToolbarView.Presenter.Appearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final ToolbarDisplayMessages displayMessages;

    public ToolbarViewPresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<ToolbarDisplayMessages> create(ToolbarDisplayMessages.class));
    }

    ToolbarViewPresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                          final ToolbarDisplayMessages displayMessages) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayMessages = displayMessages;
    }

    @Override
    public String createDelimitedFileDialogHeight() {
        return "150px";
    }

    @Override
    public String createDelimitedFileDialogWidth() {
        return "300px";
    }

    @Override
    public String done() {
        return iplantDisplayStrings.done();
    }

    @Override
    public String emptyTrash() {
        return iplantDisplayStrings.emptyTrash();
    }

    @Override
    public String emptyTrashWarning() {
        return iplantDisplayStrings.emptyTrashWarning();
    }

    @Override
    public String manageDataLinks() {
        return displayMessages.manageDataLinks();
    }

    @Override
    public int manageDataLinksDialogWidth() {
        return 500;
    }

    @Override
    public String manageDataLinksHelp() {
        return displayMessages.manageDataLinksHelp();
    }
}
