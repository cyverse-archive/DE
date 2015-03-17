package org.iplantc.de.theme.base.client.commons.comments.cells;

import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.commons.client.comments.view.cells.CommentsCell;
import org.iplantc.de.theme.base.client.commons.comments.CommentsDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.Date;

/**
 * @author jstroot
 */
public class CommentsCellDefaultAppearance implements CommentsCell.CommentsCellAppearance {

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<div style='white-space: normal;'> <p>On <b>{1}</b>, <i>{0} </i> wrote: </p> <p>{2}</p> </dvi>")
        SafeHtml commentCell(String user, String timestamp, String comment);

        @SafeHtmlTemplates.Template("<div style='white-space: normal;'> <p>On <b>{1}</b>, <i>{0} </i> wrote: </p> <p style='color:red;'>{2}</p> </dvi>")
        SafeHtml retractedCommentCell(String user, String timestamp, String comment);

    }

    private final Templates templates;
    private final CommentsDisplayStrings displayStrings;

    public CommentsCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<CommentsDisplayStrings> create(CommentsDisplayStrings.class));
    }

    CommentsCellDefaultAppearance(final Templates templates,
                                  final CommentsDisplayStrings displayStrings) {
        this.templates = templates;
        this.displayStrings = displayStrings;
    }


    @Override
    public void render(final SafeHtmlBuilder sb, final Comment value) {
        final DateTimeFormat format = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        final Date date = new Date(value.getTimestamp());
        if (value.isRetracted()) {
            value.setCommentText(displayStrings.commentRetracted());
            sb.append(templates.retractedCommentCell(value.getCommentedBy(),
                                                     format.format(date),
                                                     value.getCommentText()));
        } else {
            sb.append(templates.commentCell(value.getCommentedBy(),
                                            format.format(date),
                                            value.getCommentText()));
        }
    }
}
