package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.views.NewToolRequestFormView;
import org.iplantc.de.client.models.toolRequests.Architecture;
import org.iplantc.de.client.models.toolRequests.YesNoMaybe;

import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * @author jstroot
 */
public interface NewToolRequestFormViewFactory {
    NewToolRequestFormView createNewToolRequestFormView(ComboBox<Architecture> architectureComboBox,
                                                        ComboBox<YesNoMaybe> yesNoMaybeComboBox);
}
