package org.iplantc.de.theme.base.client.desktop.views;

import org.iplantc.de.desktop.client.views.widgets.DEFeedbackDialog;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class BaseFeedbackDialogAppearance implements DEFeedbackDialog.FeedbackAppearance {

    private final BaseFeedbackStrings strings;

    BaseFeedbackDialogAppearance(final BaseFeedbackStrings strings) {
        this.strings = strings;
    }

    public BaseFeedbackDialogAppearance() {
        this(GWT.<BaseFeedbackStrings> create(BaseFeedbackStrings.class));
    }

    @Override
    public String dialogHeight() {
        return "500";
    }

    @Override
    public String dialogWidth() {
        return "400";
    }

    @Override
    public FeedbackStrings displayStrings() {
        return strings;
    }

}
