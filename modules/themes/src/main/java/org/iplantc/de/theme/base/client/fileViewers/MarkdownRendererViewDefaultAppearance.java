package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.MarkDownRendererViewImpl;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class MarkdownRendererViewDefaultAppearance implements MarkDownRendererViewImpl.MarkdownRendererViewAppearance {
    private final IplantDisplayStrings displayStrings;
    private final IplantResources resources;

    public MarkdownRendererViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources>create(IplantResources.class));
    }

    MarkdownRendererViewDefaultAppearance(IplantDisplayStrings displayStrings,
                                          IplantResources resources) {
        this.displayStrings = displayStrings;
        this.resources = resources;
    }

    @Override
    public String backgroundColor() {
        return "#ffffff";
    }

    @Override
    public ImageResource saveBtnIcon() {
        return resources.save();
    }

    @Override
    public String saveBtnText() {
        return displayStrings.save();
    }
}
