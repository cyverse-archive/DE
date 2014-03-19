package org.iplantc.de.client.models.pipelines;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * An AutoBean interface for service Pipeline implementor details.
 * 
 * @author psarando
 * 
 */
public interface ImplementorDetails {

    public String getImplementor();

    public void setImplementor(String implementor);

    @PropertyName("implementor_email")
    public String getImplementorEmail();

    @PropertyName("implementor_email")
    public void setImplementorEmail(String email);

    public ImplementorDetailTest getTest();

    public void setTest(ImplementorDetailTest test);
}
