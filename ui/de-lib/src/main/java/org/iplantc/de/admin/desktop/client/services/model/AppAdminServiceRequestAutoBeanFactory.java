package org.iplantc.de.admin.desktop.client.services.model;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface AppAdminServiceRequestAutoBeanFactory extends AutoBeanFactory {

    AutoBean<AppCategorizeRequest> appCategorizeRequest();

    AutoBean<AppCategorizeRequest.CategoryRequest> categoryRequest();
}
