package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

import com.sencha.gxt.widget.core.client.form.ComboBox;

public final class ClearComboBoxSelectionKeyDownHandler implements KeyDownHandler {
//    private final ComboBox<?> comboBox;

    public ClearComboBoxSelectionKeyDownHandler(ComboBox<?> selectionItemsComboBox) {
//        this.comboBox = selectionItemsComboBox;
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
            // FIXME CORE-4806 Deselection of list arguments disabled
            /*for (SelectionItem si : listStore.getAll()) {
                si.setDefault(false);
            }*/
            // comboBox.clear();
            // comboBox.reset();
            // comboBox.setValue(null);
            // comboBox.redraw();
            // comboBox.setText(null);
            // ValueChangeEvent.fire(comboBox, null);
        }
    }
}