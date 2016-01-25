package org.iplantc.de.commons.client.widgets;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author aramsey
 */
public class EmptyStringValueChangeHandler implements ValueChangeHandler<String> {

    TextField field;

    public EmptyStringValueChangeHandler(TextField field) {
        this.field = field;
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        String value = event.getValue();
        if (field != null && value != null && value.isEmpty()) {
            field.setValue(null);
        }
    }
}
