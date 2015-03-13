package org.iplantc.de.theme.base.client.collaborators;

import org.iplantc.de.collaborators.client.views.ManageCollaboratorsView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.theme.base.client.grid.CheckBoxColumnDefaultAppearance.CheckBoxColumnResources;
import com.sencha.gxt.theme.base.client.grid.CheckBoxColumnDefaultAppearance.CheckBoxColumnStyle;
import com.sencha.gxt.theme.base.client.grid.CheckBoxColumnDefaultAppearance.CheckBoxColumnTemplates;

/**
 * @author jstroot
 */
public class ManageCollaboratorsViewDefaultAppearance implements ManageCollaboratorsView.Appearance {

    /**
     * This class is a copy of {@link CheckBoxColumnTemplates}, but with fields for
     * debug ids.
     */
    public interface CheckBoxColumnDebugTemplates extends XTemplates {
       @XTemplates.XTemplate("<div id='{debugId}' class='{style.hdChecker}'></div>")
        SafeHtml renderDebugHeader(CheckBoxColumnStyle style, String debugId);
    }

    private final CheckBoxColumnResources resources;
    private final CheckBoxColumnStyle style;
    private final CheckBoxColumnDebugTemplates templates;

    public ManageCollaboratorsViewDefaultAppearance() {
        this(GWT.<CheckBoxColumnResources> create(CheckBoxColumnResources.class),
             GWT.<CheckBoxColumnDebugTemplates>create(CheckBoxColumnDebugTemplates.class));
    }

    ManageCollaboratorsViewDefaultAppearance(final CheckBoxColumnResources resources,
                                             final CheckBoxColumnDebugTemplates templates) {
        this.resources = resources;
        this.style = resources.style();
        this.templates = templates;

        style.ensureInjected();
    }

    @Override
    public SafeHtml renderCheckBoxColumnHeader(String debugId) {
        // Pull in checkbox column appearance resources
        return templates.renderDebugHeader(style, debugId);
    }
}
