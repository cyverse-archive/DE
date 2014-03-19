package org.iplantc.de.commons.client.widgets;

import com.google.common.base.Strings;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.TakesValue;

import com.sencha.gxt.widget.core.client.form.ValueBaseField;

public class PreventEntryAfterLimitHandler implements KeyDownHandler {
    public static final int DEFAULT_LIMIT = 255;
    private final TakesValue<String> hasText;
    private final int limit;

    public PreventEntryAfterLimitHandler(TakesValue<String> hasText) {
        this(hasText, DEFAULT_LIMIT);
    }

    public PreventEntryAfterLimitHandler(TakesValue<String> hasText, int limit) {
        this.hasText = hasText;
        this.limit = limit;
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        String value = hasText.getValue();
        if (hasText instanceof ValueBaseField<?>) {
            value = ((ValueBaseField<String>)hasText).getCurrentValue();
        }

        int length = 0;
        if (value != null) {
            length = value.length();
        }
        boolean nullOrEmpty = Strings.isNullOrEmpty(value);
        boolean aboveLimit = length >= limit;
        boolean isDeleteBackSpaceOrNav = (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) || (event.getNativeKeyCode() == KeyCodes.KEY_DELETE) || (event.getNativeKeyCode() == KeyCodes.KEY_UP)
                || (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) || (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) || (event.getNativeKeyCode() == KeyCodes.KEY_DOWN);
        if (!nullOrEmpty && aboveLimit && !isDeleteBackSpaceOrNav) {
            event.preventDefault();
        }
    }
}