package org.iplantc.de.client.models.systemMessages;

import org.iplantc.de.client.models.sysMsgs.Message;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * @author jstroot
 */
public interface SystemMessage extends Message {

    enum Type {
        warning,
        announcement,
        maintenance
    }

    String DATE_CREATED_KEY = "date_created";
    String ACTIVATION_DATE_KEY = "activation_date";
    String DEACTIVATION_DATE_KEY = "deactivation_date";


    @PropertyName(DATE_CREATED_KEY)
    void setCreationTime(Date creationTime);

    @PropertyName(ACTIVATION_DATE_KEY)
    void setActivationTime(Date activationTime);

    @PropertyName(DEACTIVATION_DATE_KEY)
    void setDeactivationTime(Date deactivationTime);

    void setType(String type);

    @PropertyName("message")
    void setBody(String body);

    void setDismissible(boolean dismissible);
}
