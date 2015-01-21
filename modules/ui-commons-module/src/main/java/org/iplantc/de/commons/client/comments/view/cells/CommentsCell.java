package org.iplantc.de.commons.client.comments.view.cells;

import org.iplantc.de.client.models.comments.Comment;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class CommentsCell extends AbstractCell<Comment> {

    public interface CommentsCellAppearance {

        void render(SafeHtmlBuilder sb, Comment value);
    }

    private final CommentsCellAppearance appearance;

    public CommentsCell() {
        this(GWT.<CommentsCellAppearance> create(CommentsCellAppearance.class));
    }

    CommentsCell(final CommentsCellAppearance appearance) {
        this.appearance = appearance;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, Comment value, SafeHtmlBuilder sb) {
        appearance.render(sb, value);

    }

}
