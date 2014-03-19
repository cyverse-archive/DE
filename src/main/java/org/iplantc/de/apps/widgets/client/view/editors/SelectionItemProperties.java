package org.iplantc.de.apps.widgets.client.view.editors;

import org.iplantc.de.client.models.apps.integration.SelectionItem;

import com.google.gwt.editor.client.Editor.Path;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface SelectionItemProperties extends PropertyAccess<SelectionItem> {

    ModelKeyProvider<SelectionItem> id();

    ValueProvider<SelectionItem, String> name();

    ValueProvider<SelectionItem, String> display();

    ValueProvider<SelectionItem, String> description();

    ValueProvider<SelectionItem, String> value();

    @Path("default")
    ValueProvider<SelectionItem, Boolean> isDefault();

    @Path("display")
    LabelProvider<SelectionItem> displayLabel();

}
