package org.iplantc.de.theme.base.client.desktop.views;

import org.iplantc.de.desktop.client.views.widgets.DEFeedbackDialog;

import com.google.gwt.core.client.GWT;

public class BaseFeedbackDialogAppearance implements DEFeedbackDialog.FeedbackAppearance {

    private final BaseFeedbackStrings strings;

    public BaseFeedbackDialogAppearance(final BaseFeedbackStrings strings) {
        this.strings = strings;
    }

    public BaseFeedbackDialogAppearance() {
        this(GWT.<BaseFeedbackStrings> create(BaseFeedbackStrings.class));
    }

    @Override
    public FeedbackStrings displayStrings() {
        return strings;
    }

}
