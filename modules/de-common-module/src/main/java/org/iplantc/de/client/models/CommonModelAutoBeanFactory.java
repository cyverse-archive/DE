package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface CommonModelAutoBeanFactory extends AutoBeanFactory {

    AutoBean<HasId> hasId();

    AutoBean<HasPath> hasPath();

    AutoBean<UserBootstrap> bootstrap();

    AutoBean<UserSession> userSession();
}
