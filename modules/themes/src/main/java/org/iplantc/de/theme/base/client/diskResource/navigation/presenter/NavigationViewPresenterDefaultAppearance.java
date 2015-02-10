package org.iplantc.de.theme.base.client.diskResource.navigation.presenter;

import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.theme.base.client.diskResource.navigation.NavigationDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class NavigationViewPresenterDefaultAppearance implements NavigationView.Presenter.Appearance {
    private final NavigationDisplayStrings displayStrings;
    private final IplantErrorStrings iplantErrorStrings;

    public NavigationViewPresenterDefaultAppearance() {
        this(GWT.<NavigationDisplayStrings> create(NavigationDisplayStrings.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class));
    }

    NavigationViewPresenterDefaultAppearance(final NavigationDisplayStrings displayStrings,
                                             final IplantErrorStrings iplantErrorStrings) {
        this.displayStrings = displayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
    }

    @Override
    public String diskResourceDoesNotExist(String folderName) {
        return iplantErrorStrings.diskResourceDoesNotExist(folderName);
    }

    @Override
    public String retrieveFolderInfoFailed() {
        return displayStrings.retrieveFolderInfoFailed();
    }

    @Override
    public String savedFiltersRetrievalFailure() {
        return displayStrings.savedFiltersRetrievalFailure();
    }
}
