package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasLabel;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

/**
 * This interface contains all the data required to assemble a single form field in an App Wizard UI.
 * 
 * @author jstroot
 */
public interface Argument extends HasId, HasName, HasLabel, HasDescription {
    /**
     * A constant for annotating autobeans as newly created or not. Typically used via
     * {@link AutoBean#setTag(String, Object)}.
     */
    String IS_NEW = "new argument";
    String TYPE_KEY = "type";
    String SELECTION_ITEMS_KEY = "arguments";

    ArgumentType getType();

    void setType(ArgumentType type);

    Integer getOrder();

    void setOrder(Integer order);

    @PropertyName("omit_if_blank")
    Boolean isOmitIfBlank();

    @PropertyName("omit_if_blank")
    void setOmitIfBlank(Boolean omitIfBlank);

    Splittable getDefaultValue();

    void setDefaultValue(Splittable defaultValue);

    /**
     * A property used for holding the values entered via the form fields.
     * It is kept as a splittable in order to support multiple types, which
     * map one-to-one with the {@code getType()}.
     * 
     * @return
     */
    Splittable getValue();

    void setValue(Splittable value);

    @PropertyName("required")
    Boolean getRequired();

    @PropertyName("required")
    void setRequired(Boolean required);

    List<ArgumentValidator> getValidators();

    void setValidators(List<ArgumentValidator> validators);

    @PropertyName("isVisible")
    Boolean isVisible();

    @PropertyName("isVisible")
    void setVisible(Boolean visible);

    @PropertyName(SELECTION_ITEMS_KEY)
    List<SelectionItem> getSelectionItems();

    @PropertyName(SELECTION_ITEMS_KEY)
    void setSelectionItems(List<SelectionItem> arguments);

    @PropertyName("file_parameters")
    FileParameters getFileParameters();

    @PropertyName("file_parameters")
    void setFileParameters(FileParameters dataObject);

    void setId(String id);

}
