package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasDisplayText;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface SelectionItem extends HasId, HasName, HasDescription, TakesValue<String>, HasDisplayText {

    String TO_BE_REMOVED = "List of sub items to be removed";

    void setId(String id);

    @PropertyName("isDefault")
    boolean isDefault();

    @PropertyName("isDefault")
    void setDefault(boolean isDefault);
}
