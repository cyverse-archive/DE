package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.avu.Avu;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author aramsey
 */
public interface PublishAppRequest extends HasId, HasName, HasDescription {

    void setId(String id);

    @PropertyName("integration_date")
    String getIntegrationDate();

    @PropertyName("integration_date")
    void setIntegrationDate(String date);

    @PropertyName("edited_date")
    String getEditedDate();

    @PropertyName("edited_date")
    void setEditedDate(String date);

    String getDocumentation();

    void setDocumentation(String doc);

    List<String> getReferences();

    void setReferences(List<String> references);

    List<Avu> getAvus();

    void setAvus(List<Avu> avus);
}
