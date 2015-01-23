package org.iplantc.de.theme.base.client.admin.refGenome;

import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class RefGenomePresenterDefaultAppearance implements RefGenomeView.Presenter.RefGenomePresenterAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final ReferenceGenomeDisplayStrings displayStrings;

    public RefGenomePresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<ReferenceGenomeDisplayStrings> create(ReferenceGenomeDisplayStrings.class));
    }

    RefGenomePresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                               final ReferenceGenomeDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
    }

    @Override
    public String addReferenceGenomeSuccess() {
        return displayStrings.addReferenceGenome();
    }

    @Override
    public String getReferenceGenomesLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String updateReferenceGenomeSuccess() {
        return displayStrings.updateRefGenome();
    }
}
