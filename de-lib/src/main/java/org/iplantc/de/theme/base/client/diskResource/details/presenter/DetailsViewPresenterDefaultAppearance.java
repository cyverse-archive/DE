package org.iplantc.de.theme.base.client.diskResource.details.presenter;

import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.theme.base.client.diskResource.details.DetailsViewDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class DetailsViewPresenterDefaultAppearance implements DetailsView.Presenter.Appearance {
    private final DetailsViewDisplayStrings displayStrings;

    public DetailsViewPresenterDefaultAppearance() {
        this(GWT.<DetailsViewDisplayStrings> create(DetailsViewDisplayStrings.class));
    }

    DetailsViewPresenterDefaultAppearance(final DetailsViewDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;
    }

    @Override
    public String tagAttachError() {
        return displayStrings.tagAttachError();
    }

    @Override
    public String tagAttached(String name, String value) {
        return displayStrings.tagAttached(name, value);
    }

    @Override
    public String tagDetachError() {
        return displayStrings.tagDetachError();
    }

    @Override
    public String tagDetached(String value, String name) {
        return displayStrings.tagDetached(value, name);
    }
}
