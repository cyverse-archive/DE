package org.iplantc.de.theme.base.client.admin.systemMessage.presenter;

import org.iplantc.de.admin.desktop.client.systemMessage.SystemMessageView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class SystemMessagePresenterDefaultAppearance implements SystemMessageView.Presenter.SystemMessagePresenterAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final SystemMessagePresenterDisplayStrings displayStrings;

    public SystemMessagePresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<SystemMessagePresenterDisplayStrings> create(SystemMessagePresenterDisplayStrings.class));
    }

    SystemMessagePresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                            final SystemMessagePresenterDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
    }

    @Override
    public String addSystemMessageSuccessMessage() {
        return displayStrings.addSystemMessageSuccessMessage();
    }

    @Override
    public String deleteSystemMessageSuccessMessage() {
        return displayStrings.deleteSystemMessageSuccessMessage();
    }

    @Override
    public String editSystemMessageSuccessMessage() {
        return displayStrings.editSystemMessageSuccessMessage();
    }

    @Override
    public String getSystemMessagesLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }
}
