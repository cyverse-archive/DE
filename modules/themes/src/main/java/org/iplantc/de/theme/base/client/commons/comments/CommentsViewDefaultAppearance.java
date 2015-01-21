package org.iplantc.de.theme.base.client.commons.comments;

import org.iplantc.de.commons.client.comments.CommentsView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class CommentsViewDefaultAppearance implements CommentsView.CommentsViewAppearance{
    private final CommentsDisplayStrings displayStrings;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantResources iplantResources;

    public CommentsViewDefaultAppearance() {
        this(GWT.<CommentsDisplayStrings> create(CommentsDisplayStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    CommentsViewDefaultAppearance(final CommentsDisplayStrings displayStrings,
                                  final IplantDisplayStrings iplantDisplayStrings,
                                  final IplantResources iplantResources) {
        this.displayStrings = displayStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
    }

    @Override
    public String commentColumnHeader() {
        return displayStrings.commentColumnHeader();
    }

    @Override
    public int commentColumnWidth() {
        return 350;
    }

    @Override
    public ImageResource retractIcon() {
        return iplantResources.delete();
    }

    @Override
    public String retractComment() {
        return displayStrings.retract();
    }

    @Override
    public ImageResource addCommentIcon() {
        return iplantResources.add();
    }

    @Override
    public String addComment() {
        return iplantDisplayStrings.add();
    }

    @Override
    public String commentBoxEmptyText() {
        return displayStrings.commentsBoxEmptyText();
    }

    @Override
    public String commentBoxWidth() {
        return "400px";
    }

    @Override
    public String commentBoxHeight() {
        return "100px";
    }
}
