package org.iplantc.de.client.models.systemMessages;

import org.iplantc.de.client.models.sysMsgs.Message;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

public interface SystemMessage extends Message {

    @PropertyName("date_created")
    void setCreationTime(Date creationTime);

    @PropertyName("activation_date")
    void setActivationTime(Date activationTime);

    @PropertyName("deactivation_date")
    void setDeactivationTime(Date deactivationTime);

    void setType(String type);

    @PropertyName("message")
    void setBody(String body);

    void setDismissible(boolean dismissible);
}
