package org.iplantc.de.client.models.tool;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Created by aramsey on 10/30/15.
 */
public interface ToolImplementation {

    @PropertyName("implementor")
    String getImplementor();

    @PropertyName("implementor")
    void setImplementor(String implementor);

    @PropertyName("implementor_email")
    String getImplementorEmail();

    @PropertyName("implementor_email")
    void setImplementorEmail(String implementorEmail);

    @PropertyName("test")
    ToolTestData getTest();

    @PropertyName("test")
    void setTest(ToolTestData testData);
}
