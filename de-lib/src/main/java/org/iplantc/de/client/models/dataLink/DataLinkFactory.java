package org.iplantc.de.client.models.dataLink;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface DataLinkFactory extends AutoBeanFactory {
    
    AutoBean<DataLink> dataLink();
    
    AutoBean<DataLinkList> dataLinkList();

}
